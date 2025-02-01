package me.cocoblue.springrestdocssample.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import me.cocoblue.springrestdocssample.dto.UserResponseDto;
import me.cocoblue.springrestdocssample.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
class UserApiControllerGetUserTests {
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
    void getUser_Success() throws Exception {
        // Given
        Long id = 1L;
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .id(id)
                .name("test")
                .email("a@test.com")
                .phone("010-1234-5678")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userService.getUser(id)).thenReturn(ResponseEntity.ok(userResponseDto));

        // When
        ResultActions result = mockMvc.perform(get("/api/users/{id}", id));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andDo(document("get-user",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .summary("특정 User의 정보를 조회하는 Endpoint")
                                        .description("Path에 조회할 User의 Id를 지정하여 호출합니다. 존재하지 않을 경우, 404를 반환합니다.")
                                        .requestSchema(Schema.schema("UserGetRequest"))
                                        .responseSchema(Schema.schema("UserGetResponse"))
                                        .pathParameters(
                                                parameterWithName("id").description("정보를 얻을 User의 id")
                                        )
                                        .responseFields(
                                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("User's id"),
                                                fieldWithPath("name").type(JsonFieldType.STRING).description("User's name"),
                                                fieldWithPath("email").type(JsonFieldType.STRING).description("User's email"),
                                                fieldWithPath("phone").type(JsonFieldType.STRING).description("User's phone").optional(),
                                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("User's created time"),
                                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("User's updated time")
                                        )
                                        .build()
                        )
                ));

        verify(userService).getUser(anyLong());
    }

    @Test
    void getUser_NotExists() throws Exception {
        // Given
        Long id = 1L;

        when(userService.getUser(id)).thenReturn(ResponseEntity.notFound().build());

        // When
        ResultActions result = mockMvc.perform(get("/api/users/{id}", id));

        // Then
        result.andExpect(status().isNotFound())
                .andDo(document("get-user-not-exists"));

        verify(userService).getUser(anyLong());
    }
}
