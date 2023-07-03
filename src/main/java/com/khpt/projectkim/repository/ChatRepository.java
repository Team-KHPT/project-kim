package com.khpt.projectkim.repository;

import com.khpt.projectkim.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Chat c WHERE c.user = :userId")
    void deleteAllByUserId(@Param("userId") String userId);
}
