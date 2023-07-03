package com.khpt.projectkim.entity;

import lombok.*;
import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
//@TypeDef(name = "json", typeClass = JsonType.class)
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // 회사 정보들 모두 적어 (회사명, 지역, 등등)
    private String url;

    private String company;  // 회사이름

    private String title;  // 제목

    private String region; // 지역

    private String salary;  // 급여

    private String type;  // 근무 형태

    private String time;  // 근무시간

    private String education;  // 학력

    private String career;  // 경력
}
