package com.khpt.projectkim.repository;

import com.khpt.projectkim.entity.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExampleRepository extends JpaRepository<Example, Long>  {
    @Query(value = "SELECT * FROM example ORDER BY RAND() limit 3", nativeQuery = true)
    List<Example> findRandomEntities();
}
