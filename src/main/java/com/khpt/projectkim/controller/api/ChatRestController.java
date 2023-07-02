package com.khpt.projectkim.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khpt.projectkim.dto.ChatData;
import com.khpt.projectkim.entity.Chat;
import com.khpt.projectkim.service.ChatService;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    @Value("${openai.key}")
    private String token;

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

        final SseEmitter emitter = new SseEmitter();

        new Thread(() -> {
            try {
                emitter.send(SseEmitter.event().name("info").data("Processing ChatGPT request..."));

                ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                        .builder()
                        .model("gpt-3.5-turbo-16k-0613")
                        .messages(chatMessages)
                        .stream(true)
                        .build();
                Flowable<ChatCompletionChunk> chunkFlowable = openAiService.streamChatCompletion(chatCompletionRequest);
                Disposable subscription = chunkFlowable.subscribe(
                        // onNext
                        chunk -> {
                            try {
                                emitter.send(SseEmitter.event().name("message").data(chunk.getChoices().get(0).getMessage().getContent().replaceAll(" ", "%20")));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        // onError
                        emitter::completeWithError,
                        // onComplete
                        () -> {
                            try {
                                emitter.send(SseEmitter.event().name("message").data("ChatGPT processing complete."));
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
        System.out.println(chatMessage.getContent());

        session.setAttribute("chat", chatMessage.getContent());

        return new ResponseEntity<>(HttpStatus.OK);
//        String userId = session.getAttribute("user").toString();
//
//        if (chatDataList.size() > 1) {
//            ChatData lastAssistantChat = chatDataList.get(chatDataList.size() - 2);
//            chatService.addUserChats(userId, lastAssistantChat);
//        }
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
}
