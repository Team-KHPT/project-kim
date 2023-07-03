package com.khpt.projectkim.controller.web;

import com.khpt.projectkim.dto.ExtractListFromUserDto;
import com.khpt.projectkim.entity.User;
import com.khpt.projectkim.service.ChatService;
import com.khpt.projectkim.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    private final UserService userService;

    // TODO add examples. Add html code, with js

    @ModelAttribute
    public void addAttributes(HttpServletRequest request, Model model) {
        model.addAttribute("current_url", request.getRequestURL().toString());
        model.addAttribute("image", "/icons/black.png");
    }

    @GetMapping("/new")
    public String newChat(HttpSession session, HttpServletResponse response) throws IOException {
        if (session.getAttribute("user") == null) {
            System.out.println("Create new chat failed. No session");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return null;
        }
        // TODO remove chats in user
        String userId = session.getAttribute("user").toString();
        userService.clearChats(userId);

        // TODO move results to recentResults and empty results
        userService.copyResultsToRecentResultsAndClearResults(userId);

        response.sendRedirect("/chat");
        return null;
    }

    @GetMapping
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
