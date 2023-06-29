package com.khpt.projectkim.controller.web;

import com.khpt.projectkim.dto.ExtractListFromUserDto;
import com.khpt.projectkim.entity.User;
import com.khpt.projectkim.service.ChatService;
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

        model.addAttribute("recent_results", filledDto.getRecentResults());
        model.addAttribute("results", filledDto.getResults());
        model.addAttribute("chats", filledDto.getChats());

        return "chat";
    }
}
