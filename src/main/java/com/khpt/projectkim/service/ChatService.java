package com.khpt.projectkim.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khpt.projectkim.dto.ExtractListFromUserDto;
import com.khpt.projectkim.entity.Chat;
import com.khpt.projectkim.entity.User;
import com.khpt.projectkim.repository.UserRepository;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service("chatService")
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final UserRepository userRepository;

//    public ChatData getChatGptResponse(List<ChatData> chatDataList) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        try {
//            String url = "http://localhost:8000/chat";
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            String json = objectMapper.writeValueAsString(chatDataList);
//            HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
//            ResponseEntity<ChatData> res = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ChatData.class);
//            return res.getBody();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    @Transactional
    public ExtractListFromUserDto getListFromUser(String userId) {
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        ExtractListFromUserDto extractListFromUserDto = new ExtractListFromUserDto();

        int test = 0;
        extractListFromUserDto.setUser(user);
        test += user.getRecentResults().size();
        extractListFromUserDto.setRecentResults(user.getRecentResults());
        test += user.getResults().size();
        extractListFromUserDto.setResults(user.getResults());
        test += user.getChats().size();
        extractListFromUserDto.setChats(user.getChats());
        System.out.println("total size " + test);

        return extractListFromUserDto;
    }

    @Transactional
    public List<ChatMessage> getUserChats(String userId) {
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        List<Chat> chats = user.getChats();

        List<ChatMessage> chatDataList = new ArrayList<>();
        for (Chat chat : chats) {
            chatDataList.add(new ChatMessage(chat.getRole().value(), chat.getContent()));
        }
        return chatDataList;
    }

    @Transactional
    public void addUserChats(String userId, ChatMessage chatMessage) {
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Chat chat = new Chat();
        chat.setUser(user);
        chat.setRole(ChatMessageRole.valueOf(chatMessage.getRole()));
        chat.setContent(chatMessage.getContent());

        List<Chat> chatList = user.getChats();
        chatList.add(chat);
        user.setChats(chatList);

        userRepository.save(user);
    }

}
//extractListFromUserDto.setUser(user);
//        System.out.println("recent");
////        System.out.println(user.getRecentResults());
//        extractListFromUserDto.setRecentResults(user.getRecentResults());
//        System.out.println("result");
////        System.out.println(user.getResults());
//        extractListFromUserDto.setResults(user.getResults());
//        System.out.println("chat");
////        System.out.println(user.getChats());
//        extractListFromUserDto.setChats(user.getChats());