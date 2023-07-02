package com.khpt.projectkim.controller.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.khpt.projectkim.entity.User;
import com.khpt.projectkim.functions.ApiRequest;
import com.khpt.projectkim.service.ApiRequestService;
import com.khpt.projectkim.service.ChatService;
import com.khpt.projectkim.service.UserService;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.FunctionExecutor;
import com.theokanning.openai.service.OpenAiService;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

import static com.theokanning.openai.service.OpenAiService.defaultObjectMapper;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    private final ApiRequestService apiRequestService;

    private final UserService userService;

    private static final ObjectMapper mapper = defaultObjectMapper();


    @Value("${openai.key}")
    private String token;

    @Value("${saramin.key}")
    private String saramin_key;

    private OpenAiService openAiService;

    @PostConstruct
    public void init() {
        this.openAiService = new OpenAiService(token);
    }

    @GetMapping("/events")
    public SseEmitter getChatEvents(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (session.getAttribute("user") == null) {
            System.out.println("Get chat events failed. No session");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return null;
        }
        if (session.getAttribute("chat") == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        String userId = session.getAttribute("user").toString();
        List<ChatMessage> chatMessages =  chatService.getUserChats(userId);
        String chat = session.getAttribute("chat").toString();
        session.removeAttribute("chat");
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), chat);
        chatMessages.add(chatMessage);

        User user = userService.getUserByStringId(userId);

        final SseEmitter emitter = new SseEmitter();

        new Thread(() -> {
            try {
                emitter.send(SseEmitter.event().name("info").data("Processing ChatGPT request..."));

                final List<ChatFunction> functions = Collections.singletonList(ChatFunction.builder()
                        .name("get_job_info")
                        .description("Search list of job posting information with given keywords.")
                        .executor(ApiRequest.JobData.class, w -> {
                            Map<String, String> params = new HashMap<>();
                            params.put("keyword", w.getKeyword());
                            params.put("sort", String.valueOf(w.getSort()));
                            params.put("job_cd", "TODO gpt should add this params");  // TODO
                            params.put("job_type", user.getType());
                            params.put("edu_lv", user.getEducation());
                            params.put("loc_mcd", user.getRegion());
                            params.put("count", "20");
                            params.put("access-key", saramin_key);

                            return apiRequestService.getApiResponseAsString("https://oapi.saramin.co.kr/job-search", params);
                            // TODO 요청 100개중 커리어가 맞는거 20개 고르기
                        })
                        .build());
                final FunctionExecutor functionExecutor = new FunctionExecutor(functions);

                ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                        .builder()
                        .model("gpt-3.5-turbo-16k-0613")
                        .messages(chatMessages)
                        .n(1)
                        .functions(functionExecutor.getFunctions())
                        .logitBias(new HashMap<>())
                        .build();
                // TODO Save GPT response to DB

                ChatFunctionCall functionCall = new ChatFunctionCall(null, null);
                ChatMessage accumulatedMessage = new ChatMessage(ChatMessageRole.ASSISTANT.value(), null);

                Flowable<ChatCompletionChunk> chunkFlowable = openAiService.streamChatCompletion(chatCompletionRequest);
                Disposable subscription = chunkFlowable.subscribe(
                        chunk -> {
                            try {
                                ChatMessage messageChunk = chunk.getChoices().get(0).getMessage();
                                if (messageChunk.getFunctionCall() != null) {
                                    if (messageChunk.getFunctionCall().getName() != null) {
                                        String namePart = messageChunk.getFunctionCall().getName();
                                        functionCall.setName((functionCall.getName() == null ? "" : functionCall.getName()) + namePart);
                                    }
                                    if (messageChunk.getFunctionCall().getArguments() != null) {
                                        String argumentsPart = messageChunk.getFunctionCall().getArguments() == null ? "" : messageChunk.getFunctionCall().getArguments().asText();
                                        functionCall.setArguments(new TextNode((functionCall.getArguments() == null ? "" : functionCall.getArguments().asText()) + argumentsPart));
                                    }
                                    accumulatedMessage.setFunctionCall(functionCall);
                                } else {
                                    accumulatedMessage.setContent((accumulatedMessage.getContent() == null ? "" : accumulatedMessage.getContent()) + (messageChunk.getContent() == null ? "" : messageChunk.getContent()));
                                }

                                if (chunk.getChoices().get(0).getFinishReason() != null) { // last
                                    if (functionCall.getArguments() != null) {
                                        functionCall.setArguments(mapper.readTree(functionCall.getArguments().asText()));
                                        accumulatedMessage.setFunctionCall(functionCall);
                                    }
                                }

                                ChatMessage message = chunk.getChoices().get(0).getMessage();
                                if (message.getContent() != null) {
                                    emitter.send(SseEmitter.event().name("message").data(message.getContent().replaceAll(" ", "%20")));
                                } else if (message.getFunctionCall() != null) {
                                    emitter.send(SseEmitter.event().name("function").data("prepare"));
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        // onError
                        emitter::completeWithError,
                        // onComplete
                        () -> {
                            try {
                                System.out.println(accumulatedMessage);
                                System.out.println(accumulatedMessage.getRole());
                                System.out.println(accumulatedMessage.getContent());
                                System.out.println(accumulatedMessage.getFunctionCall());

                                if (accumulatedMessage.getFunctionCall() != null) {
                                    emitter.send(SseEmitter.event().name("function").data("processing"));

                                    // TODO process function

                                } else {
                                    chatService.addUserChats(userId, accumulatedMessage);
                                }


                                emitter.send(SseEmitter.event().name("info").data("ChatGPT processing complete."));
                                emitter.send(SseEmitter.event().name("complete").data("Processing complete"));
                                emitter.complete();
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        }
                );
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    // (프론트에서 채팅 보내기 클릭 하면 이 함수에 모든 채팅 기록을 줌)
    @PostMapping
    public ResponseEntity<Void> sendChat(HttpSession session, HttpServletRequest request, HttpServletResponse response, @RequestBody ChatMessage chatMessage) throws IOException {
        if (session.getAttribute("user") == null) {
            System.out.println("Send chat failed. No session");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        System.out.println(chatMessage.getRole() + chatMessage.getContent());

        String userId = session.getAttribute("user").toString();
        chatService.addUserChats(userId, chatMessage);
        session.setAttribute("chat", chatMessage.getContent());
        return new ResponseEntity<>(HttpStatus.OK);

//        ChatData lastUserChat = chatDataList.get(chatDataList.size() - 1);
//        chatService.addUserChats(userId, lastUserChat);
    }

    @GetMapping("/all")
    public List<ChatMessage> getChats(HttpSession session, HttpServletResponse response) {
        if (session.getAttribute("user") == null) {
            System.out.println("Get chat failed. No session");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return null;
        }
        String userId = session.getAttribute("user").toString();
        return chatService.getUserChats(userId);
    }

    @GetMapping("/new")
    public String newChat(HttpSession session) {
        // TODO remove chats in user
        // TODO move results to recentResults and empty results

        return "chat";
    }
}
