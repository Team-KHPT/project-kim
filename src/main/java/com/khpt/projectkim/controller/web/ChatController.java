package com.khpt.projectkim.controller.web;

import com.khpt.projectkim.dto.ExtractListFromUserDto;
import com.khpt.projectkim.dto.UserPrevData;
import com.khpt.projectkim.entity.User;
import com.khpt.projectkim.service.ChatService;
import com.khpt.projectkim.service.QuestionService;
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
import java.util.ArrayList;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    private final UserService userService;

    private final QuestionService questionService;

    // TODO check if prev data exist
    // TODO add design if no prev data is provided
    // TODO add examples. Add html code, with js

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("image", "/icons/black.png");
        model.addAttribute("chats", new ArrayList<>());
    }

    // TODO 이전 기록 가져오기 메소드

    @GetMapping("/new")
    public String newChat(HttpSession session, HttpServletResponse response) throws IOException {
        if (session.getAttribute("user") == null) {
            System.out.println("Create new chat failed. No session");
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
        UserPrevData userPrevData = userService.getUserPrevData(userid);
        if (!(userPrevData.getCategory() == null && userPrevData.getType() == null && userPrevData.getRegion() == null && userPrevData.getEducation() == null)) {
            model.addAttribute("prev_data", "1");
        }
        if (filledDto.getChats().size() == 0) {
            model.addAttribute("questions", questionService.getRandomQuestions());
        }

        return "chat";
    }
}
