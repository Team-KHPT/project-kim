package com.khpt.projectkim.controller;

import com.khpt.projectkim.entity.User;
import com.khpt.projectkim.model.ChatData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final HttpSession httpSession;

    @GetMapping("/chat")
    public String chat(Model model) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null){
            return "chat";
        }
        model.addAttribute("name", user.getName());
        model.addAttribute("image", user.getPicture());
        // TODO 현재 분석결과 세션에서 불러오기
        return "chat";
    }

    @PostMapping("/chat")
    public ChatData sendChat(@RequestParam List<ChatData> chatDataList) {
        // TODO API 요청으로 responseChat 가져오기
        ChatData responseChat = new ChatData("assistant", "안녕하세요. 무엇을 도와드릴까요?");
        return responseChat;
    }
}
