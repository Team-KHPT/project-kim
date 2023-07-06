package com.khpt.projectkim.service;

import com.khpt.projectkim.entity.Question;
import com.khpt.projectkim.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<String> getRandomQuestions() {
        List<String> questionList = questionRepository.findRandomEntities();
        System.out.println(questionList);
        return questionList;
    }
}
