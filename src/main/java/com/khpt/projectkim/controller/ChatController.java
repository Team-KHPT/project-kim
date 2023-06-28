package com.khpt.projectkim.controller;

import com.khpt.projectkim.dto.ExtractListFromUserDto;
import com.khpt.projectkim.entity.User;
import com.khpt.projectkim.service.ChatService;
import com.khpt.projectkim.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    private final UserService userService;

    @ModelAttribute
    public void addAttributes(HttpServletRequest request, Model model) {
        model.addAttribute("current_url", request.getRequestURL().toString());
        model.addAttribute("image", "/icons/black.png");
    }

    @GetMapping("/chat")
    public String chat(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            System.out.println("userid is null");
            return "chat";
        }
        String userid = session.getAttribute("user").toString();
        System.out.println(userid);

        ExtractListFromUserDto filledDto = chatService.getListFromUser(userid);

        User user = filledDto.getUser();
        System.out.println(user.getLogin());

        model.addAttribute("name", user.getLogin());
        model.addAttribute("image", user.getPicture());

        System.out.println("model");
        model.addAttribute("recent_results", filledDto.getRecentResults());
        System.out.println("model1");
        model.addAttribute("results", filledDto.getResults());
        System.out.println("model2");
        model.addAttribute("chats", filledDto.getChats());
        System.out.println("model3");

//        if (filledDto.getRecentResults().size() > 0) {
//            model.addAttribute("recent_results", user.getRecentResults());
//        }
//        if (filledDto.getResults().size() > 0) {
//            model.addAttribute("results", user.getResults());
//        }
//        if (filledDto.getChats().size() > 0) {
//            model.addAttribute("chats", user.getChats());
//        }

        // TODO 현재 분석결과 세션에서 불러오기
        return "chat";
    }
}
