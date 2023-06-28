package com.khpt.projectkim.controller.web;

import com.khpt.projectkim.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
public class ViewController {
    @GetMapping(value = {"/", "/index"})
    public String index(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if(user != null){
            model.addAttribute("name", user.getLogin());
        }
        return "index";
    }

    @GetMapping("chatTest")
    public String test(Model model) {
        Date date = new Date();
        model.addAttribute("date", date);
        return "chatTest";
    }
}
