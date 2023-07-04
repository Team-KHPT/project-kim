package com.khpt.projectkim.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPrevData {
    private String type;  // 근무 형태

    private String region; // 지역

    private String education;  // 학력

    private String career;

    private String category;  // 직업 카테고리
}
