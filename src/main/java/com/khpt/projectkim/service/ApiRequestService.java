package com.khpt.projectkim.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.khpt.projectkim.service.SimplifyJsonService.simplifyJobs;

@Service
public class ApiRequestService {

    public String getApiResponseAsString(String url, Map<String, String> params) throws JsonProcessingException {
//        RestTemplate restTemplate = new RestTemplate();
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
//
//        // Add the parameters to the request
//        for (Map.Entry<String, String> entry : params.entrySet()) {
//            builder.queryParam(entry.getKey(), entry.getValue());
//        }
//
////        HttpHeaders headers = new HttpHeaders();
////        headers.add("Accept", "application/json");
////        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
////
//////        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class, headers);
////        ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, httpEntity, String.class);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
//        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
//
//        System.out.println("url");
//        System.out.println(builder.toUriString());
//        System.out.println(response);
//
//        if (response.getStatusCodeValue() == 200) {
//            return response.getBody();
//        } else {
//            throw new RuntimeException("Failed : HTTP error code : " + response.getStatusCodeValue());
//        }
        RestTemplate restTemplate = new RestTemplate();

        // Set request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create URI with parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        params.forEach(builder::queryParam);
        URI uri = builder.build().encode().toUri();

        // Build the request
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        // Send the request as GET
        ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                String.class
        );

        System.out.println("response:");
        System.out.println(response.getBody());
        System.out.println(response.getHeaders());


        return response.getBody();
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
