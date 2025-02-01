# Spring Restdocs Sample
이 프로젝트는 Spring Restdocs와 Swagger UI를 연동하여 사용하는 방법을 보여주기 위한 샘플 프로젝트입니다.

## Environment
- Java 17
- Spring Boot 3.2.2
- Spring Restdocs
- Spring Data JPA
- H2 Database
- Gradle 8.5
- JUnit 5
- Swagger UI

## Endpoints
- 사용자 목록 조회 (GET /api/users)
- 특정 사용자 조회 (GET /api/users/{id})
- 새 사용자 생성 (POST /api/users)
- 사용자 정보 수정 (PUT /api/users/{id})
- 사용자 삭제 (DELETE /api/users/{id})

## API Documentation with Swagger UI
Swagger UI를 통해 API 문서를 확인할 수 있습니다.
- <URL>/docs/swagger

## References
[스프링부트3 Spring REST docs + Swagger UI 사용하기 By.E@st](https://jun27.tistory.com/65)  
[OpenAPI Specification을 이용한 더욱 효과적인 API 문서화 By.KakaoPay](https://tech.kakaopay.com/post/openapi-documentation/)