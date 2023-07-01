package com.khpt.projectkim.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khpt.projectkim.dto.ChatData;
import com.khpt.projectkim.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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

    // (프론트에서 채팅 보내기 클릭 하면 이 함수에 모든 채팅 기록을 줌)
    @PostMapping
    public ChatData sendChat(HttpSession session, HttpServletRequest request, HttpServletResponse response, @RequestBody List<ChatData> chatDataList) throws IOException {
        if (session.getAttribute("user") == null) {
            System.out.println("Send chat failed. No session");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return null;
        }

        String userId = session.getAttribute("user").toString();

        if (chatDataList.size() > 1) {
            ChatData lastAssistantChat = chatDataList.get(chatDataList.size() - 2);
            chatService.addUserChats(userId, lastAssistantChat);
        }

        ChatData lastUserChat = chatDataList.get(chatDataList.size() - 1);
        chatService.addUserChats(userId, lastUserChat);

        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        try {
            String json = objectMapper.writeValueAsString(chatDataList);

            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

            String url = "http://localhost:8000/chat";
            ResponseEntity<ChatData> res = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ChatData.class);

            return res.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return new ChatData("assistant", "ERROR");
        }
    }

    @GetMapping("/all")
    public List<ChatData> getChats(HttpSession session) {
        ChatData responseChat1 = new ChatData("assistant", "안녕하세요. 무엇을 도와드릴까요?111");
        ChatData responseChat2 = new ChatData("assistant", "안녕하세요. 무엇을 도와드릴까요?222");
        ChatData responseChat3 = new ChatData("assistant", "안녕하세요. 무엇을 도와드릴까요?33333");
        List<ChatData> chats = new ArrayList<>();
        chats.add(responseChat1);
        chats.add(responseChat2);
        chats.add(responseChat3);
        return chats;
    }
}
