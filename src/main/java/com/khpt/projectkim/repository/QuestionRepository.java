package com.khpt.projectkim.repository;

import com.khpt.projectkim.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;


public interface QuestionRepository extends JpaRepository<Question, Long> {

        @Query(value = "SELECT question FROM question ORDER BY RAND() limit 3", nativeQuery = true)
        List<String> findRandomEntities();
}
