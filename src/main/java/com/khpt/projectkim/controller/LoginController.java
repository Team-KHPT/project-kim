package com.khpt.projectkim.controller;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginController {

    public String url = "";

    @GetMapping("")
    public String loginPage(@RequestParam("redirect_url") String url) {
        this.url = url;
        return "login";
    }


    @GetMapping("/{provider}")
    public void oauthLogin(HttpServletResponse response, @PathVariable String provider) throws IOException {
        response.sendRedirect("/oauth2/authorization/" + provider);
    }


//    @GetMapping("/social/{provider}")
//    public void login(HttpServletResponse response, @PathVariable String provider) throws IOException {
//        response.sendRedirect("/oauth2/authorization/" + provider);
//    }
//
//    @GetMapping("/authorized")
//    public ResponseEntity<String> authorized(@AuthenticationPrincipal CustomOAuth2User user) {
//        // 디버깅해서 확인해보면 scope 내에 있던 정보들 다 들어가 있는 거 볼 수 있다
//        OAuthAttributes attributes = user.getOAuthAttributes();
//        return ResponseEntity.ok("대충 토큰 발급했다고 치자.");
//    }

}