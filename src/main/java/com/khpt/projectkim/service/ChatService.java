package com.khpt.projectkim.service;

import com.khpt.projectkim.dto.ChatData;
import com.khpt.projectkim.dto.ExtractListFromUserDto;
import com.khpt.projectkim.entity.Chat;
import com.khpt.projectkim.entity.User;
import com.khpt.projectkim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("chatService")
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final UserRepository userRepository;

    @Transactional
    public ExtractListFromUserDto getListFromUser(String userid) {
        User user = userRepository.findById(Long.parseLong(userid)).orElseThrow();

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

    public User updateUserChats(Long userId, ChatData chatData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        List<Chat> chatList = user.getChats();

        Chat chat = new Chat();
        chat.setUser(user);
        chat.setRole(chatData.getRole());
        chat.setContent(chatData.getContent());

        chatList.add(chat);


        user.setChats(chatList);

        return userRepository.save(user);
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