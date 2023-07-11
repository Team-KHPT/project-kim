package com.khpt.projectkim.service;

import com.khpt.projectkim.dto.ResultDto;
import com.khpt.projectkim.dto.UserPrevData;
import com.khpt.projectkim.entity.Result;
import com.khpt.projectkim.entity.User;
import com.khpt.projectkim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public User getUserByStringId(String id) {
        return userRepository.findById(Long.parseLong(id)).orElseThrow();
    }

    public boolean userHasNoPrevData(User user) {
        Boolean isAllNull = user.getCategory() == null && user.getType() == null && user.getEducation() == null && user.getRegion() == null;
        Boolean isAllBlank = Objects.equals(user.getCategory(), "") && Objects.equals(user.getType(), "") && Objects.equals(user.getEducation(), "") && Objects.equals(user.getRegion(), "");
        return isAllNull || isAllBlank;
    }

    @Transactional
    public void copyResultsToRecentResultsAndClearResults(String id) {
        User user = getUserByStringId(id);

        // Make a copy of the results
        List<Result> resultsCopy = new ArrayList<>(user.getResults());

        if (resultsCopy.size() > 0) {
            // Clear the recentResults and add the copied results
            user.getRecentResults().clear();
            user.getRecentResults().addAll(resultsCopy);

            // Clear the results
            user.getResults().clear();

            // Save the user back to the database
            userRepository.save(user);

//            user.setRecentResults(resultsCopy);
//
//            user.getResults().clear();
//
//            userRepository.save(user);
        }
    }

    @Transactional
    public void clearChats(String id) {
        User user = getUserByStringId(id);

        log.debug("chats size {}", user.getChats().size());
        if (user.getChats().size() > 0) {
            user.getChats().clear();

            userRepository.save(user);
        }
    }


    public void setUserResults(String id, Map<String, List<Map<String, Object>>> results) {
        User user = getUserByStringId(id);

        List<Result> saveResults = new ArrayList<>();
        for (Map<String, Object> r : results.get("jobs")) {
            Result result = new Result();
            result.setCompany(r.get("company").toString());
            result.setEducation(r.get("education").toString());
            result.setRegion(r.get("location").toString());
            result.setSalary(r.get("salary").toString());
            result.setTitle(r.get("title").toString());
            result.setType(r.get("type").toString());
            result.setUrl(r.get("url").toString());
            result.setCareer(r.get("experience_level").toString());
            result.setUser(user);
            saveResults.add(result);
        }

        user.setResults(saveResults);
        userRepository.save(user);
    }

    @Transactional
    public List<ResultDto> getUserResultsAsDto(String id) {
        // Get the user
        User user = getUserByStringId(id);

        // Get the user's results
        List<Result> results = user.getResults();

        // Convert the results to a list of ResultDto
        List<ResultDto> resultDtos = results.stream().map(result -> {
            ResultDto dto = new ResultDto();
            dto.setCompany(result.getCompany());
            dto.setEducation(result.getEducation());
            dto.setLocation(result.getRegion());
            dto.setSalary(result.getSalary());
            dto.setTitle(result.getTitle());
            dto.setType(result.getType());
            dto.setUrl(result.getUrl());
            dto.setExperience_level(result.getCareer());
            return dto;
        }).collect(Collectors.toList());

        log.debug("results size {}", resultDtos.size());

        // Return the list of ResultDto objects
        return resultDtos;
    }

    public void setUserPrevData(String id, UserPrevData userPrevData) {
        User user = getUserByStringId(id);

        user.setType(userPrevData.getType());
        user.setRegion(userPrevData.getRegion());
        user.setEducation(userPrevData.getEducation());
        user.setCareer(userPrevData.getCareer());
        user.setCategory(userPrevData.getCategory());

        userRepository.save(user);
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
