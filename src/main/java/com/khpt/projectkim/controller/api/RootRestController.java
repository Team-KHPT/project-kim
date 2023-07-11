package com.khpt.projectkim.controller.api;

import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class RootRestController {
    @GetMapping("/")
    public void root(Device device, HttpServletResponse response) throws IOException {
        if (device.isMobile()) {
            response.sendRedirect("/m/chat");
        } else {
            response.sendRedirect("/chat");
        }
    }
}
