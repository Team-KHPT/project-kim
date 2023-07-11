package com.khpt.projectkim.controller.api;

import com.khpt.projectkim.dto.ResultDto;
import com.khpt.projectkim.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ResultRestController {

    private final UserService userService;

    @GetMapping("/result")
    public List<ResultDto> getResult(HttpSession session, HttpServletResponse response) throws IOException {
        if (session.getAttribute("user") == null) {
            log.debug("Result: Get result failed. No session");
            response.sendRedirect("/");
            return null;
        }
        String userId = session.getAttribute("user").toString();

        return userService.getUserResultsAsDto(userId);
    }
}
