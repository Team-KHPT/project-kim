package com.khpt.projectkim.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.khpt.projectkim.csv.CsvReader;
import com.khpt.projectkim.entity.User;
import com.khpt.projectkim.functions.ApiRequest;
import com.khpt.projectkim.service.ApiRequestService;
import com.khpt.projectkim.service.ChatService;
import com.khpt.projectkim.service.SimplifyJsonService;
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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

import static com.khpt.projectkim.service.SimplifyJsonService.simplifyJobs;
import static com.khpt.projectkim.service.SimplifyJsonService.simplifyJobs2;
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
    public SseEmitter getChatEvents(HttpSession session, HttpServletResponse response) {
        // TODO 프로세스 확인

        // TODO 프롬프트 엔지니어링

        // TODO 예시 페이지 만들기

        if (session.getAttribute("user") == null) {
            System.out.println("Get chat events failed. No session");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return null;
        }
        if (session.getAttribute("chat") == null) {
            System.out.println("event is found, but no chat");
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        String userId = session.getAttribute("user").toString();

        String chat = session.getAttribute("chat").toString();
        session.removeAttribute("chat");

        List<ChatMessage> chatMessages =  chatService.getUserChats(userId);
        chatMessages.add(0, Prompts.CHAT_PROMPT.message());
        chatMessages.add(1, Prompts.CHAT_PROMPT2.message());
        chatMessages.add(new ChatMessage(ChatMessageRole.USER.value(), chat));

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
                            if (w.getKeyword() != null) params.put("keywords", w.getKeyword());
                            if (w.getSort() != null) params.put("sort", String.valueOf(w.getSort()));
                            if (w.getCodes() != null) params.put("job-cd", w.getCodes());  // TODO 작동 하는지 확인
                            if (user.getCategory() != null) params.put("job-mid-cd", user.getCategory());
                            if (user.getType() != null) params.put("job_type", user.getType());
                            if (user.getEducation() != null) params.put("edu_lv", user.getEducation());
                            if (user.getRegion() != null) params.put("loc_mcd", user.getRegion());
                            params.put("count", "100");
                            params.put("access-key", saramin_key);

                            String apiResponse;
                            try {
                                apiResponse = apiRequestService.getApiResponseAsString("https://oapi.saramin.co.kr/job-search", params);
                                ObjectMapper mapper = new ObjectMapper();
                                SimplifyJsonService.Root root = mapper.readValue(apiResponse, SimplifyJsonService.Root.class);

                                Map<String, List<Map<String, Object>>> simplifiedJobs = simplifyJobs(root, user.getCareer(), 15);
                                System.out.print("Jobs found with API: ");
                                System.out.println(root.jobs.job.size());
                                System.out.print("Simplified job count: ");
                                System.out.println(simplifiedJobs.get("jobs").size());

                                Map<String, List<Map<String, Object>>> simplifiedJobs2 = simplifyJobs2(root, user.getCareer(), 15);
                                userService.setUserResults(userId, simplifiedJobs2);

                                return mapper.writeValueAsString(simplifiedJobs);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .build());
                final FunctionExecutor functionExecutor = new FunctionExecutor(functions);

                ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                        .builder()
                        .model("gpt-3.5-turbo-16k-0613")
                        .messages(chatMessages)
                        .n(1)
                        .maxTokens(512)
                        .functions(functionExecutor.getFunctions())
                        .logitBias(new HashMap<>())
                        .build();

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
                                    emitter.send(SseEmitter.event().name("message").data(message.getContent().replaceAll(" ", "%20").replaceAll("\n", "%0A")));
                                } else if (message.getFunctionCall() != null) {
                                    emitter.send(SseEmitter.event().name("process").data("Preparing API request"));
                                }
                            } catch (IOException e) {
                                emitter.send(SseEmitter.event().name("err").data("ChatGPT 응답중 에러"));
                                emitter.completeWithError(e);
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
                                    emitter.send(SseEmitter.event().name("process").data("Processing API request"));

                                    if (!(user.getType() != null && user.getRegion() != null && user.getEducation() != null && user.getCategory() != null)) {
                                        emitter.send(SseEmitter.event().name("err").data("사전정보를 입력해주세요"));  // TODO add prev error msg to front
                                        emitter.send(SseEmitter.event().name("complete").data("error executing function"));
                                        emitter.send(SseEmitter.event().name("process").data("Error"));
                                        emitter.complete();
                                        return;
                                    }

                                    ChatMessage callResponse = functionExecutor.executeAndConvertToMessageHandlingExceptions(accumulatedMessage.getFunctionCall());
                                    if (callResponse.getName().equals("error")) {
                                        emitter.send(SseEmitter.event().name("err").data("사람인 API 요청을 실패하였습니다."));
                                        emitter.send(SseEmitter.event().name("complete").data("error executing function"));
                                        emitter.send(SseEmitter.event().name("process").data("Error"));
                                        emitter.complete();
                                        return;
                                    }

                                    emitter.send(SseEmitter.event().name("process").data("fine"));
                                    emitter.send(SseEmitter.event().name("result").data("result ready"));

                                    // TODO add job codes table
                                    chatMessages.add(new ChatMessage(
                                            ChatMessageRole.SYSTEM.value(),
                                            "Below is job_code_table."
                                    ));
                                    chatMessages.add(new ChatMessage(
                                            ChatMessageRole.SYSTEM.value(),
                                            CsvReader.getDetailedJobCode(user.getCategory())
                                    ));

                                    chatMessages.add(accumulatedMessage);
                                    chatMessages.add(callResponse);
                                    chatMessages.add(Prompts.FUNCTIONS_PROMPT.message());

                                    ChatCompletionRequest chatCompletionRequest2 = ChatCompletionRequest
                                            .builder()
                                            .model("gpt-3.5-turbo-16k-0613")
                                            .messages(chatMessages)
                                            .maxTokens(1024)
                                            .functions(functionExecutor.getFunctions())
                                            .logitBias(new HashMap<>())
                                            .build();

                                    ChatFunctionCall functionCall2 = new ChatFunctionCall(null, null);
                                    ChatMessage accumulatedMessage2 = new ChatMessage(ChatMessageRole.ASSISTANT.value(), null);

                                    Flowable<ChatCompletionChunk> chunkFlowable2 = openAiService.streamChatCompletion(chatCompletionRequest2);
                                    Disposable subscription2 = chunkFlowable2.subscribe(
                                            chunk -> {
                                                try {
                                                    ChatMessage messageChunk = chunk.getChoices().get(0).getMessage();
                                                    if (messageChunk.getFunctionCall() != null) {
                                                        if (messageChunk.getFunctionCall().getName() != null) {
                                                            String namePart = messageChunk.getFunctionCall().getName();
                                                            functionCall2.setName((functionCall2.getName() == null ? "" : functionCall2.getName()) + namePart);
                                                        }
                                                        if (messageChunk.getFunctionCall().getArguments() != null) {
                                                            String argumentsPart = messageChunk.getFunctionCall().getArguments() == null ? "" : messageChunk.getFunctionCall().getArguments().asText();
                                                            functionCall2.setArguments(new TextNode((functionCall2.getArguments() == null ? "" : functionCall2.getArguments().asText()) + argumentsPart));
                                                        }
                                                        accumulatedMessage2.setFunctionCall(functionCall2);
                                                    } else {
                                                        accumulatedMessage2.setContent((accumulatedMessage2.getContent() == null ? "" : accumulatedMessage2.getContent()) + (messageChunk.getContent() == null ? "" : messageChunk.getContent()));
                                                    }

                                                    if (chunk.getChoices().get(0).getFinishReason() != null) { // last
                                                        if (functionCall2.getArguments() != null) {
                                                            functionCall2.setArguments(mapper.readTree(functionCall2.getArguments().asText()));
                                                            accumulatedMessage2.setFunctionCall(functionCall2);
                                                        }
                                                    }

                                                    ChatMessage message = chunk.getChoices().get(0).getMessage();
                                                    if (message.getContent() != null) {
                                                        emitter.send(SseEmitter.event().name("message").data(message.getContent().replaceAll(" ", "%20").replaceAll("\n", "%0A")));
                                                    }
                                                } catch (IOException e) {
                                                    emitter.send(SseEmitter.event().name("err").data("ChatGPT functions 응답중 에러"));
                                                    emitter.completeWithError(e);
                                                    throw new RuntimeException(e);
                                                }
                                            },
                                            // onError
                                            emitter::completeWithError,
                                            // onComplete
                                            () -> {
                                                try {
                                                    // Save GPT response to DB
                                                    chatService.addUserChats(userId, accumulatedMessage2);

                                                    emitter.send(SseEmitter.event().name("info").data("ChatGPT processing complete."));
                                                    emitter.send(SseEmitter.event().name("complete").data("Processing complete"));
                                                    emitter.complete();
                                                    System.out.println("Save chat with functions");
                                                } catch (IOException e) {
                                                    System.out.println("complete error3");
                                                    emitter.send(SseEmitter.event().name("err").data("채팅 저장중 에러"));
                                                    emitter.completeWithError(e);
                                                }
                                            }
                                    );


                                } else {
                                    // Save GPT response to DB
                                    chatService.addUserChats(userId, accumulatedMessage);
                                    emitter.send(SseEmitter.event().name("info").data("ChatGPT processing complete."));
                                    emitter.send(SseEmitter.event().name("complete").data("Processing complete"));
                                    emitter.complete();
                                    System.out.println("Save chat without functions");
                                }
                            } catch (IOException e) {
                                System.out.println("complete error2");
                                emitter.send(SseEmitter.event().name("err").data("ChatGPT 응답 처리 중 에러"));
                                emitter.completeWithError(e);
                            }
                        }
                );
            } catch (IOException e) {
                System.out.println("complete error1");
                emitter.completeWithError(e);
            }
        }).start();

        System.out.println("return emitter");
        return emitter;
    }

    // (프론트에서 채팅 보내기 클릭 하면 이 함수에 모든 채팅 기록을 줌)
    @PostMapping
    public ResponseEntity<Void> sendChat(HttpSession session, HttpServletResponse response, @RequestBody ChatMessage chatMessage) {
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
    }

    @GetMapping("/all")
    public List<ChatMessage> getChats(HttpSession session, HttpServletResponse response) throws IOException {
        if (session.getAttribute("user") == null) {
            System.out.println("Get chat failed. No session");
            response.sendRedirect("/");
            return null;
        }
        String userId = session.getAttribute("user").toString();
        return chatService.getUserChats(userId);
    }

    @GetMapping("/test")
    public String test(@RequestParam("cd") String cd) {
        String result = CsvReader.getDetailedJobCode(cd);
        System.out.println(result);
        return result;
    }
}
