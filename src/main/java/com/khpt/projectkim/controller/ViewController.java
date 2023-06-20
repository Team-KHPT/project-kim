package com.khpt.projectkim.controller;

import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index(){
        return "test";
    }

    @GetMapping("/login")
    public String test(){
        return "oauthTest";
    }

    @PostMapping("/")
    public String index(String message, Model model){
        System.out.println(message);
        model.addAttribute("message", message);
        return "test";
    }

    @RequestMapping("/")
    public String oauth() {
        return "oauthTest";
    }

}
