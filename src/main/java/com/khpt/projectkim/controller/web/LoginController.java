package com.khpt.projectkim.controller.web;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {
    @GetMapping
    public String loginPage(HttpSession session, HttpServletResponse response, Model model, @RequestParam(value = "redirect", defaultValue = "/") String uri) throws IOException {
        if (session.getAttribute("user") != null) {
            log.info("Login: already login");
            response.sendRedirect("/");
        }

        log.info("Login: page show");

        model.addAttribute("redirect_uri", uri);
        return "login";
    }

    @GetMapping("/{provider}")
    public void oauthLogin(HttpServletRequest request, HttpServletResponse response, @PathVariable String provider, @RequestParam("redirect") String uri) throws IOException {
        request.getSession().setAttribute("redirect", uri);
        response.sendRedirect("/oauth2/authorization/" + provider + "?redirect=" + uri);
    }

}