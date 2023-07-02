package com.khpt.projectkim.service;

import com.khpt.projectkim.dto.UserPrevData;
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

    public void setUserPrevData(String id, UserPrevData userPrevData) {
        User user = getUserByStringId(id);

        String type = userPrevData.getType();
        String region = userPrevData.getRegion();
        String education = userPrevData.getEducation();
        String career = userPrevData.getCareer();
        String category = userPrevData.getCategory();

        user.updatePrevData(type, region, education, career, category);
    }

    public UserPrevData getUserPrevData(String id) {
        User user = getUserByStringId(id);

        UserPrevData userPrevData = new UserPrevData();
        userPrevData.setType(user.getType());
        userPrevData.setRegion(user.getRegion());
        userPrevData.setEducation(user.getEducation());
        userPrevData.setCareer(user.getCareer());
        userPrevData.setCategory(user.getCategory());

        return userPrevData;
    }
}
