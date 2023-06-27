package com.khpt.projectkim.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public enum ChatAuthor {
    SYSTEM("system"),
    ASSISTANT("assistant"),
    USER("user");

    private final String value;
}
