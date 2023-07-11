package com.khpt.projectkim.controller.web;

import com.khpt.projectkim.dto.ExtractListFromUserDto;
import com.khpt.projectkim.entity.User;
import com.khpt.projectkim.service.ChatService;
import com.khpt.projectkim.service.QuestionService;
import com.khpt.projectkim.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    private final UserService userService;

    private final QuestionService questionService;

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("image", "/icons/black.png");
        model.addAttribute("chats", new ArrayList<>());
    }

    // TODO 이전 기록 가져오기 메소드

    @GetMapping("/new")
    public String newChat(HttpSession session, HttpServletResponse response) throws IOException {
        if (session.getAttribute("user") == null) {
            log.info("Chat new: Create new chat failed. No session");
            response.sendRedirect("/");
            return null;
        }
        // remove chats in user
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
            log.info("Chat: without login");
            return "chat";
        }
        String userid = session.getAttribute("user").toString();

        ExtractListFromUserDto filledDto = chatService.getListFromUser(userid);

        User user = filledDto.getUser();
        log.info("{} Chat: with login {}", userid, user.getLogin());

        model.addAttribute("name", user.getLogin());
        model.addAttribute("image", user.getPicture());

        model.addAttribute("recent_results", filledDto.getRecentResults());
        model.addAttribute("results", filledDto.getResults());
        model.addAttribute("chats", filledDto.getChats());

        if (!userService.userHasNoPrevData(user)) {
            model.addAttribute("prev_data", "1");
        }
        if (filledDto.getChats().size() == 0) {
            model.addAttribute("questions", questionService.getRandomQuestions());
        }

        return "chat";
    }
}
