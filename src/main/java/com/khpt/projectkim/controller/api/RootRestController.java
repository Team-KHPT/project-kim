package com.khpt.projectkim.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class RootRestController {
    @GetMapping("/")
    public void root(HttpServletResponse response) throws IOException {
        response.sendRedirect("/chat");
    }
}
