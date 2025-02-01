package me.cocoblue.springrestdocssample.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import me.cocoblue.springrestdocssample.dto.PaginatedResponseDto;
import me.cocoblue.springrestdocssample.dto.UserResponseDto;
import me.cocoblue.springrestdocssample.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
class UserApiControllerGetUsersTests {
    @Autowired
    private MockMvc mockMvc;

    // Spring Boot 3.4.0 이상에서는 @MockBean 대신 @MockitoSpyBean을 사용해야 함
    // Spring Boot 3.4.0 미만일 시, @MockBean을 사용할 것.
    @MockitoSpyBean
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUsers_Success_NextCursorNotExists() throws Exception {
        assertTrue(Mockito.mockingDetails(userService).isSpy());

        final PaginatedResponseDto<UserResponseDto> paginatedResponse = PaginatedResponseDto.<UserResponseDto>builder()
                .data(List.of(UserResponseDto.builder()
                        .id(1L)
                        .name("TestUser")
                        .email("@test.com")
                        .phone("010-1234-5678")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()))
                .hasNext(false)
                .build();

        doReturn(ResponseEntity.ok(paginatedResponse))
                .when(userService).getUsersList(anyLong(), anyInt());

        mockMvc.perform(get("/api/users")
                        .param("cursor", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                // then: 응답 결과를 검증
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.hasNext").value(false))
                .andDo(document("users-get-without-nextCursor",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .summary("User 리스트를 조회하는 Endpoint")
                                        .description("사용자 목록을 조회합니다. 커서 값이 주어지면 해당 커서부터 데이터를 불러옵니다.")
                                        .requestSchema(Schema.schema("UserListRequest"))
                                        .responseSchema(Schema.schema("UserListResponse"))
                                        .queryParameters(
                                                parameterWithName("cursor").description("커서 값").optional(),
                                                parameterWithName("size").description("불러올 항목의 개수 (1~100)").optional().attributes(key("Default").value("10"))
                                        )
                                        .responseFields(
                                                fieldWithPath("data").description("사용자 목록 (비어 있을 수 있음)").type(JsonFieldType.ARRAY).optional(),
                                                fieldWithPath("data[].id").description("사용자 ID").type(JsonFieldType.NUMBER),
                                                fieldWithPath("data[].name").description("사용자 이름").type(JsonFieldType.STRING),
                                                fieldWithPath("data[].email").description("사용자 이메일").type(JsonFieldType.STRING),
                                                fieldWithPath("data[].phone").description("사용자 전화번호").optional().type(JsonFieldType.STRING),
                                                fieldWithPath("data[].createdAt").description("사용자 생성일").type(JsonFieldType.STRING),
                                                fieldWithPath("data[].updatedAt").description("사용자 정보 수정일").optional().type(JsonFieldType.STRING),
                                                fieldWithPath("nextCursor").description("다음 페이지 커서 값").optional().type(JsonFieldType.NUMBER),
                                                fieldWithPath("hasNext").description("다음 페이지 존재 여부").optional().type(JsonFieldType.BOOLEAN)
                                        )
                                        .build()
                        )
                ));

        verify(userService).getUsersList(anyLong(), anyInt());
    }

    @Test
    void getUsers_Success_NextCursorExists() throws Exception {
        // given: 다음 페이지가 존재하는 상황
        final PaginatedResponseDto<UserResponseDto> paginatedResponse = PaginatedResponseDto.<UserResponseDto>builder()
                .data(List.of(
                        UserResponseDto.builder()
                                .id(1L)
                                .name("TestUser1")
                                .email("user1@test.com")
                                .phone("010-1111-1111")
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build(),
                        UserResponseDto.builder()
                                .id(2L)
                                .name("TestUser2")
                                .email("user2@test.com")
                                .phone("010-2222-2222")
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build()))
                .nextCursor(3L)
                .hasNext(true)
                .build();

        doReturn(ResponseEntity.ok(paginatedResponse))
                .when(userService).getUsersList(anyLong(), anyInt());

        // when & then
        mockMvc.perform(get("/api/users")
                        .param("cursor", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.nextCursor").value(3L)) // nextCursor 검증
                .andExpect(jsonPath("$.hasNext").value(true))  // hasNext 검증
                .andDo(document("users-get-with-nextCursor"));

        verify(userService).getUsersList(anyLong(), anyInt());
    }


    @Test
    void getUsers_SizeExceedsMax_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/users")
                        .param("size", "1000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors[0]").value("getUsers.size: must be less than or equal to 100"))
                .andDo(document("users-get-invalid-size"));

        // userService.getUsersList()가 호출되지 않았는지 확인
        verify(userService, times(0)).getUsersList(anyLong(), anyInt());
    }
}
