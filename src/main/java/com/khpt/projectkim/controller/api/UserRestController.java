package com.khpt.projectkim.controller.api;

import com.khpt.projectkim.dto.UserPrevData;
import com.khpt.projectkim.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @PostMapping("/prev")
    public ResponseEntity<Void> setPrevData(HttpSession session, @RequestBody UserPrevData userPrevData) {
        if (session.getAttribute("user") == null) {
            System.out.println("Set prev data failed. No session");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        System.out.println(userPrevData.getType());
        System.out.println(userPrevData.getCategory());
        System.out.println(userPrevData.getEducation());
        System.out.println(userPrevData.getRegion());

        String userId = session.getAttribute("user").toString();
        userService.setUserPrevData(userId, userPrevData);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/prev")
    public UserPrevData getPrevData(HttpSession session, HttpServletResponse response) throws IOException {
        if (session.getAttribute("user") == null) {
            System.out.println("Get prev data failed. No session");
            response.sendRedirect("/");
            return null;
        }
        String userId = session.getAttribute("user").toString();
        return userService.getUserPrevData(userId);
    }
}
