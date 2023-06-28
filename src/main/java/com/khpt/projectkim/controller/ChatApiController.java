package com.khpt.projectkim.controller;

import com.khpt.projectkim.dto.ChatData;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatApiController {
    @PostMapping
    public ChatData sendChat(HttpSession session, @RequestBody List<ChatData> chatDataList) {
        // TODO API 요청으로 responseChat 가져오기
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
