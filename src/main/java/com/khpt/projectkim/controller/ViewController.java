package com.khpt.projectkim.controller;

import com.khpt.projectkim.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class ViewController {
    @GetMapping(value = {"/", "/index"})
    public String index(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if(user != null){
            model.addAttribute("userName", user.getName());
        }
        return "index";
    }
}
