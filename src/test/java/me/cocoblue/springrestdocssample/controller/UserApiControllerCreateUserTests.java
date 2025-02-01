package me.cocoblue.springrestdocssample.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.cocoblue.springrestdocssample.dto.UserCreateRequestDto;
import me.cocoblue.springrestdocssample.dto.UserCreateResponseDto;
import me.cocoblue.springrestdocssample.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
class UserApiControllerCreateUserTests {
    @Autowired
    private MockMvc mockMvc;

    // Spring Boot 3.4.0 이상에서는 @MockBean 대신 @MockitoSpyBean을 사용해야 함
    // Spring Boot 3.4.0 미만일 시, @MockBean을 사용할 것.
    @MockitoSpyBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_Success() throws Exception {
        assertTrue(Mockito.mockingDetails(userService).isSpy());

        final UserCreateRequestDto userCreateRequestDto = UserCreateRequestDto.builder()
                .name("test")
                .email("a@test.com")
                .phone("010-1234-5678")
                .build();

        final LocalDateTime createdAt = LocalDateTime.now();
        final UserCreateResponseDto responseDto = UserCreateResponseDto.builder()
                .id(1L)
                .createdAt(createdAt)
                .build();

        doReturn(ResponseEntity.ok(responseDto))
                .when(userService).createUser(any(userCreateRequestDto.getClass()));

        mockMvc.perform(post("/api/users")
                        .content(objectMapper.writeValueAsString(userCreateRequestDto))
                        .contentType("application/json")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andDo(document("create-users",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .summary("User를 생성하는 Endpoint")
                                        .description("Body에 생성할 User 데이터를 담아서 호출합니다. Email은 중복될 수 없습니다.")
                                        .requestSchema(Schema.schema("UserCreateRequest"))
                                        .responseSchema(Schema.schema("UserCreateResponse"))
                                        .requestFields(
                                                fieldWithPath("name").description("사용자 이름").type(JsonFieldType.STRING),
                                                fieldWithPath("email").description("사용자 이메일").type(JsonFieldType.STRING),
                                                fieldWithPath("phone").description("사용자 전화번호 (- 포함)").optional().type(JsonFieldType.STRING)
                                        )
                                        .responseFields(
                                                fieldWithPath("id").description("생성된 사용자 ID").type(JsonFieldType.NUMBER),
                                                fieldWithPath("createdAt").description("사용자 생성 완료 시간").type(JsonFieldType.STRING)
                                        )
                                        .build()
                        )
                ));

        verify(userService).createUser(any(UserCreateRequestDto.class));
    }

    @Test
    void createUser_Failure_Name_Is_Null() throws Exception {
        assertTrue(Mockito.mockingDetails(userService).isSpy());

        final UserCreateRequestDto userCreateRequestDto = UserCreateRequestDto.builder()
                .name(null)
                .email("a@test.com")
                .phone("010-1234-5678")
                .build();

        final LocalDateTime createdAt = LocalDateTime.now();
        final UserCreateResponseDto userCreateResponseDto = UserCreateResponseDto.builder()
                .id(1L)
                .createdAt(createdAt)
                .build();

        doReturn(ResponseEntity.ok(userCreateResponseDto))
                .when(userService).createUser(any(UserCreateRequestDto.class));

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userCreateRequestDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andDo(document("create-users-fail-name-is-null")
                );
    }

    @Test
    void createUser_Failure_Name_Is_Blank() throws Exception {
        assertTrue(Mockito.mockingDetails(userService).isSpy());

        final UserCreateRequestDto userCreateRequestDto = UserCreateRequestDto.builder()
                .name("")
                .email("a@test.com")
                .phone("010-1234-5678")
                .build();

        final LocalDateTime createdAt = LocalDateTime.now();
        final UserCreateResponseDto userCreateResponseDto = UserCreateResponseDto.builder()
                .id(1L)
                .createdAt(createdAt)
                .build();

        doReturn(ResponseEntity.ok(userCreateResponseDto))
                .when(userService).createUser(any(UserCreateRequestDto.class));

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userCreateRequestDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andDo(document("create-users-fail-name-is-blank")
                );
    }

    @Test
    void createUser_Failure_Phone_Is_Blank() throws Exception {
        assertTrue(Mockito.mockingDetails(userService).isSpy());

        final UserCreateRequestDto userCreateRequestDto = UserCreateRequestDto.builder()
                .name("a")
                .email("a@test.com")
                .phone("")
                .build();

        final LocalDateTime createdAt = LocalDateTime.now();
        final UserCreateResponseDto userCreateResponseDto = UserCreateResponseDto.builder()
                .id(1L)
                .createdAt(createdAt)
                .build();

        doReturn(ResponseEntity.ok(userCreateResponseDto))
                .when(userService).createUser(any(UserCreateRequestDto.class));

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userCreateRequestDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andDo(document("create-users-fail-phone-is-blank")
                );
    }

    @Test
    void createUser_Failure_Phone_Not_Match_Pattern() throws Exception {
        assertTrue(Mockito.mockingDetails(userService).isSpy());

        final UserCreateRequestDto userCreateRequestDto = UserCreateRequestDto.builder()
                .name("a")
                .email("a@test.com")
                .phone("02123456789")
                .build();

        final LocalDateTime createdAt = LocalDateTime.now();
        final UserCreateResponseDto userCreateResponseDto = UserCreateResponseDto.builder()
                .id(1L)
                .createdAt(createdAt)
                .build();

        doReturn(ResponseEntity.ok(userCreateResponseDto))
                .when(userService).createUser(any(UserCreateRequestDto.class));

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userCreateRequestDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andDo(document("create-users-fail-phone-not-match-pattern")
                );
    }

    @Test
    void createUser_Failure_Duplicated_Email() throws Exception {
        assertTrue(Mockito.mockingDetails(userService).isSpy());

        final UserCreateRequestDto userCreateRequestDto = UserCreateRequestDto.builder()
                .name("a")
                .email("a@test.com")
                .phone("02-1234-6789")
                .build();

        doReturn(ResponseEntity.status(HttpStatus.CONFLICT).build())
                .when(userService).createUser(any(UserCreateRequestDto.class));

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userCreateRequestDto))
                )
                .andExpect(status().isConflict())
                .andDo(document("create-users-fail-duplicated-email")
                );

        verify(userService).createUser(any(UserCreateRequestDto.class));
    }
}
