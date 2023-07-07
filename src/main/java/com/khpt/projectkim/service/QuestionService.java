package com.khpt.projectkim.service;

import com.khpt.projectkim.entity.Example;
import com.khpt.projectkim.entity.Question;
import com.khpt.projectkim.repository.ExampleRepository;
import com.khpt.projectkim.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    private final ExampleRepository exampleRepository;

    public List<String> getRandomQuestions() {
        List<String> questionList = questionRepository.findRandomEntities();
        System.out.println(questionList);
        return questionList;
    }

    public List<Example> getRandomExamples() {
        List<Example> exampleList = exampleRepository.findRandomEntities();
        System.out.println(exampleList);
        return exampleList;
    }

    public Example getExampleByStringId(String id) {
        return exampleRepository.findById(Long.parseLong(id)).orElse(new Example());
    }
}
