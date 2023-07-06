package com.khpt.projectkim.controller.api;

import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;

public enum Prompts {
    CHAT_PROMPT(ChatMessageRole.SYSTEM, "You're a job counselor. Give job counseling to the user. You can provide the job information accordingly. Recruitment information will be displayed on the right side of the user's screen, so you don't have to mention it yourself. You are the one who summarizes the information."),
    CHAT_PROMPT2(ChatMessageRole.SYSTEM, "답변은 모두 한국말로 해야해"),
    FUNCTIONS_PROMPT(ChatMessageRole.SYSTEM, "만약 채용 정보가 없다면 사용자에게 사전 정보의 범위를 더 늘리거나 다르게 검색해달라고 요청해줘. 그리고 채용 정보를 사용자에게 말해줄때는 사용자의 필요에 맞는 채용 정보만을 요약해서 말해줘. 한번에 3 가지 이상의 채용정보를 말하지 마.");

    private final ChatMessageRole role;
    private final String value;

    Prompts(ChatMessageRole role, String value) {
        this.role = role;
        this.value = value;
    }

    public String value() {
        return value;
    }

    public ChatMessage message() {
        return new ChatMessage(role.value(), value);
    }
}
