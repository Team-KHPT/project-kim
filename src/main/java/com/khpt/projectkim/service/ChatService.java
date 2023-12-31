package com.khpt.projectkim.service;

import com.khpt.projectkim.dto.ExtractListFromUserDto;
import com.khpt.projectkim.entity.Chat;
import com.khpt.projectkim.entity.User;
import com.khpt.projectkim.repository.ChatRepository;
import com.khpt.projectkim.repository.UserRepository;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service("chatService")
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final UserService userService;

    private final UserRepository userRepository;

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
        log.debug("{} ChatService: total size {}", userId, test);

        return extractListFromUserDto;
    }

    public void deleteLastTwoChat(String userId) {
        User user = userService.getUserByStringId(userId);

        List<Chat> chatItems = user.getChats();
        if (chatItems.size() >= 2) {
            if (chatItems.get(chatItems.size() - 1).getRole() == ChatMessageRole.ASSISTANT) {
                chatItems.remove(chatItems.size() - 1);
                chatItems.remove(chatItems.size() - 1);
            } else {
                chatItems.remove(chatItems.size() - 1);
            }

        }
        user.setChats(chatItems);
        userRepository.save(user);
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
        chat.setRole(ChatMessageRole.valueOf(chatMessage.getRole().toUpperCase()));
        chat.setContent(chatMessage.getContent());

        List<Chat> chatList = user.getChats();
        chatList.add(chat);
        user.setChats(chatList);

        userRepository.save(user);
    }

}
