package com.khpt.projectkim.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultDto {
    private String url;

    private String company;  // 회사이름

    private String title;  // 제목

    private String location; // 지역

    private String salary;  // 급여

    private String type;  // 근무 형태

    private String education;  // 학력

    private String experience_level;  // 경력
}
