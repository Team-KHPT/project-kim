package com.khpt.projectkim.controller.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Prompts {
    CHAT_PROMPT("You're a job counselor. Give job counseling to the user. You can provide the job information accordingly. Recruitment information will be displayed on the right side of the user's screen, so you don't have to mention it yourself. You are the one who summarizes the information."),
    CHAT_PROMPT2("답변은 모두 한국말로 해야해"),
    FUNCTIONS_PROMPT("만약 채용 정보가 없거나 5개 이하라면 사용자에게 사전 정보의 범위를 더 높여달라고 요청해줘");

    private final String value;
}
