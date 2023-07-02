package com.khpt.projectkim.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class ApiRequestService {

    public String getApiResponseAsString(String url, Map<String, String> params) {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        // Add the parameters to the request
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        System.out.println(builder.toUriString());


        if (response.getStatusCodeValue() == 200) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatusCodeValue());
        }
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
