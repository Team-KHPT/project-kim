package com.khpt.projectkim.controller.api;

import com.khpt.projectkim.dto.ResultDto;
import com.khpt.projectkim.service.ApiRequestService;
import com.khpt.projectkim.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResultRestController {

    private final ApiRequestService apiRequestService;

    private final UserService userService;

    @GetMapping("/result")
    public List<ResultDto> getResult(HttpSession session, HttpServletResponse response) {
        if (session.getAttribute("user") == null) {
            System.out.println("Get chat failed. No session");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return null;
        }
        String userId = session.getAttribute("user").toString();

        return userService.getUserResultsAsDto(userId);
    }
}
