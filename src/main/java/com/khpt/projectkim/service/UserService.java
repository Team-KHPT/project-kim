package com.khpt.projectkim.service;

import com.khpt.projectkim.entity.User;
import com.khpt.projectkim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public boolean isMember(OAuth2User principal) {
        String userId = principal.getName();
//        if(userRepository.findByGithubId(userId) == null) {
            System.out.println(userId);
            return false;
//        }
    }

}
