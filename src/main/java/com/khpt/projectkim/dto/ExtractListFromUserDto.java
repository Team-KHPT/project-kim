package com.khpt.projectkim.dto;

import com.khpt.projectkim.entity.Chat;
import com.khpt.projectkim.entity.Result;
import com.khpt.projectkim.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExtractListFromUserDto {
    private String userid;

    private User user;

    private List<Result> results;

    private List<Result> recentResults;

    private List<Chat> chats;
}
