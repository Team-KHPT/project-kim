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
    private final HttpSession httpSession;

    @GetMapping(value = {"/", "/index"})
    public String index(Model model) {
        User user = (User) httpSession.getAttribute("user");
        if(user != null){
            model.addAttribute("userName", user.getName());
        }
        return "index";
    }
}
