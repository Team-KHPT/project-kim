package com.khpt.projectkim.entity;

import com.theokanning.openai.completion.chat.ChatMessageRole;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // system or assistant or user
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatMessageRole role;

    // chat messages
    @Column(nullable = false, length = 2048)
    private String content;

}
