package com.khpt.projectkim.config;

import com.khpt.projectkim.controller.LoginController;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {
    private String url;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        new LoginController().url = url;
        if(url.equals("chat")) response.sendRedirect("/chat");
        else response.sendRedirect("/chat/analysis");
    }
}
