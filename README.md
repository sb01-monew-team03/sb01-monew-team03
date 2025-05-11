# <span><img src="https://notion-emojis.s3-us-west-2.amazonaws.com/prod/svg-twitter/1f4f0.svg" width="30" height="30"/></span> 모뉴 / 3조

[![CI](https://github.com/sb01-monew-team03/sb01-monew-team03/actions/workflows/ci.yaml/badge.svg)](https://github.com/sb01-monew-team03/sb01-monew-team03/actions/workflows/ci.yaml)
[![Codecov](https://codecov.io/gh/sb01-monew-team03/sb01-monew-team03/graph/badge.svg?token=NY2BMVH8VF)](https://codecov.io/gh/sb01-monew-team03/sb01-monew-team03)

## 팀원 R&R

|                                                   이유빈                                                   |                                                   김도일                                                    |                                                   이병규                                                   |                                                   이승주                                                   |                                                   한성지                                                    |
|:-------------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------:|
| <span><img src="https://avatars.githubusercontent.com/u/80386881?v=4" width="150" height="150"/></span> | <span><img src="https://avatars.githubusercontent.com/u/207847768?v=4" width="150" height="150"/></span> | <span><img src="https://avatars.githubusercontent.com/u/93171052?v=4" width="150" height="150"/></span> | <span><img src="https://avatars.githubusercontent.com/u/61524863?v=4" width="150" height="150"/></span> | <span><img src="https://avatars.githubusercontent.com/u/158116933?v=4" width="150" height="150"/></span> |
|                                  [@iiyubb](https://github.com/iiyubb)                                   |                                 [@doil1130](https://github.com/doil1130)                                 |                                 [@naron88](https://github.com/naron88)                                  |                                [@leesj092](https://github.com/leesj092)                                 |                                  [@hyanyul](https://github.com/hyanyul)                                  |
|                                                   팀장                                                    |                                                    팀원                                                    |                                                   팀원                                                    |                                                   팀원                                                    |                                                    팀원                                                    |
|                                           알림 관리<br/>활동 내역 관리                                            |                                                  댓글 관리                                                   |                                              사용자 관리<br/>배포                                              |                                                뉴스 기사 관리                                                 |                                             관심사 관리<br/>로그 관리                                             |

<br/>

## 프로젝트 소개

**모뉴**는 여러 뉴스 API를 통합하여 **사용자에게 맞춤형 뉴스를 제공**하고, 의견을 나눌 수 있는 **소셜 기능**을 갖춘 서비스입니다.

[🔗**모뉴 배포 사이트**](http://54.180.31.220)

<br/>

## 기술 스택

### 📌 Back-End

- **Java 17+**
- **Spring Boot**

    - Spring Web (REST API)
    - Spring Data JPA (Hibernate)
    - Spring Validation (Bean Validation)
    - Spring Batch
    - Spring Actuator (헬스체크, 메트릭)
    - Spring Boot Test (JUnit 5 포함)

- **QueryDSL 5** (Jakarta)
- **SpringDoc OpenAPI 3** (Swagger UI)

### 🗄️ Database

- **PostgreSQL** (운영/개발 환경)
- **H2 Database** (테스트 환경)
- **MongoDB** (사용자 활동 내역)

### ⚙️ 개발 도구 및 유틸리티

- **Lombok + MapStruct** (코드 간결화 및 DTO ↔ Entity 매핑)
- **Apache Commons Text** (문자열 유틸리티)
- **dotenv-java** (.env 환경변수 관리)

### ☁️ 인프라 & 스토리지

- **AWS S3 SDK** (파일 업로드/다운로드)

<br/>

## 폴더 구조

전반적인 구성은 **계층형 아키텍처(layered architecture)** 를 따르되,  
각 계층 내부는 **기능(도메인) 중심으로 세분화**하여 유지보수성과 가독성을 높였습니다.

```angular2html
src
├─main
│  ├─java
│  │  └─team03
│  │      └─monew
│  │          ├─config
│  │          │  ├─api
│  │          │  └─batch
│  │          ├─controller
│  │          │  ├─activity
│  │          │  ├─comments
│  │          │  ├─interest
│  │          │  ├─notification
│  │          │  └─user
│  │          ├─dto
│  │          │  ├─article
│  │          │  ├─comments
│  │          │  ├─common
│  │          │  ├─interest
│  │          │  ├─notification
│  │          │  └─user
│  │          ├─entity
│  │          │  ├─activity
│  │          │  ├─article
│  │          │  ├─base
│  │          │  ├─comments
│  │          │  ├─interest
│  │          │  ├─notification
│  │          │  └─user
│  │          ├─event
│  │          │  └─subscription
│  │          ├─mapper
│  │          │  ├─article
│  │          │  ├─comments
│  │          │  ├─interest
│  │          │  ├─notification
│  │          │  └─user
│  │          ├─repository
│  │          │  ├─article
│  │          │  ├─comments
│  │          │  ├─interest
│  │          │  ├─notification
│  │          │  └─user
│  │          ├─service
│  │          │  ├─activity
│  │          │  ├─article
│  │          │  ├─comments
│  │          │  ├─interest
│  │          │  ├─log
│  │          │  ├─notification
│  │          │  └─user
│  │          └─util
│  │              ├─exception
│  │              │  ├─article
│  │              │  ├─comments
│  │              │  ├─interest
│  │              │  ├─notification
│  │              │  ├─subscription
│  │              │  └─user
│  │              └─interest
│  └─resources
│      └─static
│          └─assets
└─test    # test 폴더는 main과 동일하게 구성
```