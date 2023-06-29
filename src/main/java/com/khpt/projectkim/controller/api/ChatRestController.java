package com.khpt.projectkim.controller.api;

import com.khpt.projectkim.dto.ChatData;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatRestController {


    // (프론트에서 채팅 보내기 클릭 하면 이 함수에 모든 채팅 기록을 줌)
    @PostMapping
    public ChatData sendChat(HttpSession session, HttpServletResponse response, @RequestBody List<ChatData> chatDataList) {
        if (session.getAttribute("user") == null) {
            System.out.println("Send chat failed. No session");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return null;
        }

        // ChatData 클래스는 채팅 하나를 의미함
        // 여러개의 채팅이 모여 chatDataList 에 모든 채팅 내역이 순서대로 있음

        // 물론 이 리스트의 마지막 아이템에 유저가 새로 입력한 채팅이 있음
        // TODO chatDataList의 젤 마지막 아이템을 사용자 레포지토리 채팅 기록에 추가

        // TODO chatDataList의 젤 마지막 아이템을 JSON으로 변환

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
