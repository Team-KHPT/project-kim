package com.khpt.projectkim.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("chatTest")
    public String test() {
        return "chatTest";
    }
}
