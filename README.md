# 김비서 : 사용자 맞춤 취업 AI 비서

---

안녕하세요, Team. KHTP입니다.

저희 팀은 취업 정보 수집에 대한 어려움을 ChatGPT와 사람인 API를 활용하여 chatGPT 대화 주도 방식을 통해 사용자에게 정보를 수집해 사용자 맞춤형 기업을 추천해주는 서비스를 개발했습니다.

**서비스 주소**

[**https://kim.gbsw.hs.kr/chat**](https://kim.gbsw.hs.kr/chat)

**Github 주소**

[**https://github.com/Team-KHPT/project-kim**](https://github.com/Team-KHPT/project-kim)

**서비스 가이드**

[https://khpt.notion.site/8e800c5f860e459fbeac84a61f64da5d](https://www.notion.so/8e800c5f860e459fbeac84a61f64da5d?pvs=21)

## **서비스 소개**

---

‘김비서’는 채팅을 통해 사용자가 보유하고 있는 기술과 희망하는 직종, 스펙 등을 분석해 사용자에 맞는 회사를 추천해 주는 대화형 인공지능입니다.

### **제작 배경**

저희는 현재 특성화 고등학교에 다니며 이제 취업을 준비하는 시기가 왔습니다. 현재 저희의 가장 큰 관심사인 **'취업'**과 가장 핫한 **'ChatGPT'**를 결합한 결과물이 바로 이 김비서 프로젝트 입니다.

![취업 정보를 찾기 어렵다는 자료](https://raw.githubusercontent.com/Team-KHPT/Team-KHPT.github.io/main/resources/Untitled.png)

취업 정보를 찾기 어렵다는 자료

### **사용자 층**

- 특성화 고등학교에 재학 중인 학생
- 마이스터 고등학교 재학 중인 학생
- 취업 담당 선생님
- 취업을 준비하고 있는 취준생

### **목표**

**'김비서'**는 국내 직업계고 학생들과 취준생 등 직장을 찾는 사람들이 시간을 아끼고 본인과 맞는 직장을 찾을 수 있도록 최고의 서포트를 하는 것을 목표로 합니다.

### **기대 효과**

- 사용자가 선택할 수 있는 회사의 폭이 늘어나 실업률이 낮아진다.
- 좋은 조건을 가지고 있는 중소기업들의 구인이 쉬워진다.
- 직업계 고등학교를 다니고 있는 고등학생과 사회초년생들의 취업 고민을 덜어준다.

### **향후 계획(발전 가능성)**

- 데이터를 추가하여 초,중,고등학생들을 위한 진로 상담 AI 개발
- 챗봇 형식으로 변형하여 사람인, 잡코리아 등과 같은 메이저 취업 사이트에 취업 상담 챗봇으로 탑재
- 사용자의 피드백을 받아 GPT Fine-tuning

## **Team. KHPT**

---

- 김강현
- 김동혁
- 김윤현

## 사용기술

---

- Spring Boot
- Tailwind CSS
- JavaScript
- Open API
- Saramin API
- Thymeleaf

## 프로젝트 진행 과정(월별 진행상황)

---

***색을 칠할까 요약해서 적을까***

|  | 프로젝트 기획 | 디자인 | 프론트엔드 개발 | 백엔드 개발 |
| --- | --- | --- | --- | --- |
| 4월 | O | O |  |  |
| 5월 | O | O | O | O |
| 6월 |  | O | O | O |
| 7월 |  |  | O | O |

|  | 4월 | 5월 | 6월 | 7월 |
| --- | --- | --- | --- | --- |
| 프로젝트 기획 | O |  |  |  |
| 디자인 |  | O | O |  |
| 프론트엔드 개발 |  | O | O | O |
| 백엔드 개발 |  | O | O | O |

## 프로젝트 상세 소개

---

- 사용자가 kim.gbsw.hs.kr에 접속하면 처음 화면이 나옵니다.

![처음 화면, 더 나은 정보를 제공하기 위해 채팅 예시를 보여줍니다.](https://raw.githubusercontent.com/Team-KHPT/Team-KHPT.github.io/main/resources/Untitled%201.png)

처음 화면, 더 나은 정보를 제공하기 위해 채팅 예시를 보여줍니다.

- 가이드에 따라 채팅을 작성하면 채팅 전체가 백엔드로 전달되어 저장이 됩니다.

![대화를 진행하면 사용자를 분석하여 회사 채용 정보가 표시됩니다.](https://raw.githubusercontent.com/Team-KHPT/Team-KHPT.github.io/main/resources/Untitled%202.png)

대화를 진행하면 사용자를 분석하여 회사 채용 정보가 표시됩니다.

- 채팅 하기 전 사전 정보를 작성하고 사전 정보를 수정할 수 있습니다.

![사전 정보 수정 페이지, 분석 정확도를 높혀 주는 사전 정보를 수정할 수 있습니다.](https://raw.githubusercontent.com/Team-KHPT/Team-KHPT.github.io/main/resources/Untitled%203.png)

사전 정보 수정 페이지, 분석 정확도를 높혀 주는 사전 정보를 수정할 수 있습니다.

- 채팅을 통해 사용자의 정보가 수집 되면 오른쪽에 회사 채용 정보가 표시됩니다.

![상세 정보화면, 회사들의 정보를 클릭하면 상세 정보를 볼 수 있습니다.](https://raw.githubusercontent.com/Team-KHPT/Team-KHPT.github.io/main/resources/Untitled%204.png)

상세 정보화면, 회사들의 정보를 클릭하면 상세 정보를 볼 수 있습니다.

## 프로젝트 추진 결과

---

- 수행 과정 및 결과 분석
- 유지 보수 계획
- 이전 채팅도 볼 수 있는 기능을 추가할 것
- 사람인 정보와 더불어 잡코리아나 잡플래닛을 정보도 추가하여 더 많은 채용 정보를 보여줄 수 있도록 개선할 것