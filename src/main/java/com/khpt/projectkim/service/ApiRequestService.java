package com.khpt.projectkim.service;

import org.springframework.stereotype.Service;

@Service
public class ApiRequestService {

    public void getResult() {

    }

    public String testService() {
        try {
            Thread.sleep(2 * 1000);
            System.out.println("2 sec");
            Thread.sleep(2 * 1000);
            System.out.println("4 sec");
            Thread.sleep(2 * 1000);
            System.out.println("6 sec");
            Thread.sleep(2 * 1000);
            System.out.println("8 sec");
            Thread.sleep(2 * 1000);
            System.out.println("10 sec");
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        return "This is response!";
    }
}
