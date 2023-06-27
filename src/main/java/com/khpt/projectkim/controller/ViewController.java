package com.khpt.projectkim.controller;

import com.khpt.projectkim.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class ViewController {
    private HttpSession session;
    @GetMapping(value = {"/", "/index"})
    public String index(Model model) {
        User user = (User) session.getAttribute("user");
        if(user != null){
            model.addAttribute("userName", user.getName());
        }
        return "index";
    }
}
