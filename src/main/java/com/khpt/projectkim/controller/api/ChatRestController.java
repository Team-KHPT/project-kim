package com.khpt.projectkim.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khpt.projectkim.dto.ChatData;
import com.khpt.projectkim.entity.Chat;
import com.khpt.projectkim.entity.Result;
import com.khpt.projectkim.service.ChatService;
import com.khpt.projectkim.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatRestController {

    private ChatService chatService;

    // (프론트에서 채팅 보내기 클릭 하면 이 함수에 모든 채팅 기록을 줌)
    @PostMapping
    public ChatData sendChat(HttpSession session, HttpServletResponse response, @RequestBody List<ChatData> chatDataList) {
//        if (session.getAttribute("user") == null) {
//            System.out.println("Send chat failed. No session");
//            response.setStatus(HttpStatus.BAD_REQUEST.value());
//            return null;
//        }
        ChatData lastChat = chatDataList.get(chatDataList.size() - 1);

        Long userID = (Long) session.getAttribute("user");

        chatService.updateUserChats(userID, lastChat);

        try {
            System.setProperty("https.protocols", "TLSv1.2");

            // List를 JSON 형태로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(chatDataList);

            // RestTemplate 인스턴스 생성
            RestTemplate restTemplate = new RestTemplate();

            // ChatData 객체 생성
            ChatData chatData = chatDataList.get(0);

            // 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 요청 엔티티 생성
            HttpEntity<ChatData> requestEntity = new HttpEntity<>(chatData, headers);

            // POST 요청 보내기
            String url = "http://localhost:8090/result";
            ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

            // 응답 데이터 가져오기
            String responseBody = res.getBody();
            System.out.println("Response: " + responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ChatData 클래스는 채팅 하나를 의미함
        // 여러개의 채팅이 모여 chatDataList 에 모든 채팅 내역이 순서대로 있음

        // 물론 이 리스트의 마지막 아이템에 유저가 새로 입력한 채팅이 있음
        // TODO chatDataList의 젤 마지막 아이템을 사용자 레포지토리 채팅 기록에 추가

        // TODO chatDataList JSON으로 변환

        // TODO 변환된 JSON을 API에 보내서 responseChat(GPT 응답) 가져오기

        ChatData responseChat = new ChatData("assistant", "안녕하세요. 무엇을 도와드릴까요?");
        return responseChat;
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
