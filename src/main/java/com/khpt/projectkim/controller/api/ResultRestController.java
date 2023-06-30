package com.khpt.projectkim.controller.api;

import com.khpt.projectkim.dto.ResultDto;
import com.khpt.projectkim.service.ApiRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResultRestController {

    private final ApiRequestService apiRequestService;

    @GetMapping("/api/chat")
    public SseEmitter result() {
        final SseEmitter emitter = new SseEmitter();

        // Run in separate thread to avoid request timeout
        new Thread(() -> {
            try {
                // Start processing and send initial status to frontend
                emitter.send(SseEmitter.event().name("message").data("Processing ChatGPT request..."));

                String chatGptResponse = apiRequestService.testService();
                emitter.send(SseEmitter.event().name("message").data("ChatGPT processing complete. Weather API processing..."));

                String weatherApiResponse = apiRequestService.testService();
                emitter.send(SseEmitter.event().name("message").data("Weather API processing complete."));

                // Send the responses to the frontend
                emitter.send(SseEmitter.event().name("message").data(chatGptResponse));
                emitter.send(SseEmitter.event().name("message").data(weatherApiResponse));

                emitter.send(SseEmitter.event().name("complete").data("Processing complete"));
                emitter.complete();
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    @GetMapping("/result")
    public List<ResultDto> getResult_test(HttpSession session) {
        ResultDto responseChat1 = new ResultDto("https://google.com", "comp", "제목", "지역", "대충급여", "대충형태", "대충시간", "몰?루", "경력");
        ResultDto responseChat2 = new ResultDto("https://google.com", "comp2", "제목2", "지역2", "대충급여2", "대충형태22", "대충시간22", "몰?루22", "경력22");
        ResultDto responseChat3 = new ResultDto("https://google.com", "comp3", "제목3", "지역3", "대충급여3", "대충형태33", "대충시간33", "몰?루33", "경력33");
        List<ResultDto> chats = new ArrayList<>();
        chats.add(responseChat1);
        chats.add(responseChat2);
        chats.add(responseChat3);
        return chats;
    }
}
