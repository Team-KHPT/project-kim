package com.khpt.projectkim.controller.api;

import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;

public enum Prompts {
    CHAT_PROMPT(ChatMessageRole.SYSTEM, "You're a job counselor. Give job counseling to the user. You can provide the job information accordingly. Recruitment information will be displayed on the right side of the user's screen, so you don't have to mention it yourself. You are the one who summarizes the information."),
    CHAT_PROMPT2(ChatMessageRole.SYSTEM, "답변은 모두 한국말로 해야해"),
    FUNCTIONS_PROMPT(ChatMessageRole.SYSTEM, "만약 채용 정보가 없거나 5개 이하라면 사용자에게 사전 정보의 범위를 더 늘려달라고 요청해줘");

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
