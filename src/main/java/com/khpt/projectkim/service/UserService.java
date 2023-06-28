package com.khpt.projectkim.service;

import com.khpt.projectkim.entity.User;
import com.khpt.projectkim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserByStringId(String id) {
        return userRepository.findById(Long.parseLong(id)).orElseThrow();
    }
}
