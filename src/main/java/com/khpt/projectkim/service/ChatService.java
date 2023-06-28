package com.khpt.projectkim.service;

import com.khpt.projectkim.dto.ExtractListFromUserDto;
import com.khpt.projectkim.entity.User;
import com.khpt.projectkim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("chatService")
@RequiredArgsConstructor
public class ChatService {

    private final UserRepository userRepository;

    @Transactional
    public ExtractListFromUserDto getListFromUser(String userid) {
        User user = userRepository.findById(Long.parseLong(userid)).orElseThrow();

        ExtractListFromUserDto extractListFromUserDto = new ExtractListFromUserDto();

        extractListFromUserDto.setUser(user);
        extractListFromUserDto.setRecentResults(user.getRecentResults());
        extractListFromUserDto.setResults(user.getResults());
        extractListFromUserDto.setChats(user.getChats());

        return extractListFromUserDto;
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