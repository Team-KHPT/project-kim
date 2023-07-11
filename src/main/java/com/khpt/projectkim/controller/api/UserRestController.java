package com.khpt.projectkim.controller.api;

import com.khpt.projectkim.dto.UserPrevData;
import com.khpt.projectkim.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserRestController {

    private final UserService userService;

    @PostMapping("/prev")
    public ResponseEntity<Void> setPrevData(HttpSession session, @RequestBody UserPrevData userPrevData) {
        if (session.getAttribute("user") == null) {
            log.debug("User prev: Set prev data failed. No session");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String userId = session.getAttribute("user").toString();
        userService.setUserPrevData(userId, userPrevData);

        log.debug("{} type: {}", userId, userPrevData.getType());
        log.debug("{} cate: {}", userId, userPrevData.getCategory());
        log.debug("{} educ: {}", userId, userPrevData.getEducation());
        log.debug("{} regi: {}", userId, userPrevData.getRegion());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/prev")
    public UserPrevData getPrevData(HttpSession session, HttpServletResponse response) throws IOException {
        if (session.getAttribute("user") == null) {
            log.debug("User prev: Get prev data failed. No session");
            response.sendRedirect("/");
            return null;
        }
        String userId = session.getAttribute("user").toString();
        return userService.getUserPrevData(userId);
    }
}
