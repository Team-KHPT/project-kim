package com.khpt.projectkim.controller;

import com.khpt.projectkim.entity.User;
import com.khpt.projectkim.model.ChatData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ChatController {

    @Value("${spring.datasource.url}")
    private String debug;
    @GetMapping("/chat")
    public String chat(HttpSession session, Model model) {
        System.out.println("\n\n\n\nDEBUG!!" + debug + "\n\n\n");

        User user = (User) session.getAttribute("user");
        if (user == null){
            model.addAttribute("image", "/icons/black.png");
            return "chat";
        }
        model.addAttribute("name", user.getLogin());
        model.addAttribute("image", user.getPicture());
        // TODO 현재 분석결과 세션에서 불러오기
        return "chat";
    }

    @PostMapping("/chat")
    public ChatData sendChat(HttpSession session, @RequestParam List<ChatData> chatDataList) {
        // TODO API 요청으로 responseChat 가져오기
        ChatData responseChat = new ChatData("assistant", "안녕하세요. 무엇을 도와드릴까요?");
        return responseChat;
    }
}
