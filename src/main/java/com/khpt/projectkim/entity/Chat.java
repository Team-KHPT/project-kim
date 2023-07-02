package com.khpt.projectkim.entity;

import com.theokanning.openai.completion.chat.ChatMessageRole;
import lombok.*;

import javax.persistence.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // system or assistant or user
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatMessageRole role;

    // chat messages
    @Column(nullable = false)
    private String content;

}
