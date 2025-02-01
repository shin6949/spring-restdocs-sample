package me.cocoblue.springrestdocssample.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.cocoblue.springrestdocssample.dto.UserUpdateRequestDto;
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

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
class UserApiControllerUpdateUserTests {
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
    void updateUser_Success() throws Exception {
        assertTrue(Mockito.mockingDetails(userService).isSpy());

        final UserUpdateRequestDto userUpdateRequestDto = UserUpdateRequestDto.builder()
                .name("test")
                .email("a@test.com")
                .phone("010-1234-5678")
                .build();

        doReturn(ResponseEntity.noContent().build())
                .when(userService).updateUser(anyLong(), any(UserUpdateRequestDto.class));

        mockMvc.perform(put("/api/users/{id}", 1)
                        .content(objectMapper.writeValueAsString(userUpdateRequestDto))
                        .contentType("application/json")
                )
                .andExpect(status().isNoContent())
                .andDo(document("update-user",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .summary("User의 정보를 업데이트하는 Endpoint")
                                        .description("특정 User의 Id를 Path로 지정하고, Body에 업데이트할 정보를 담아서 호출합니다.")
                                        .requestSchema(Schema.schema("UserUpdateRequest"))
                                        .responseSchema(Schema.schema("UserUpdateResponse"))
                                        .pathParameters(
                                                parameterWithName("id").description("업데이트 할 User의 id").attributes(key("type").value("number"))
                                        )
                                        .requestSchema(Schema.schema("UserUpdateRequest"))
                                        .requestFields(
                                                fieldWithPath("name").type(JsonFieldType.STRING).description("업데이트할 유저의 이름. null로 설정 시, 업데이트 하지 않음.").optional(),
                                                fieldWithPath("email").type(JsonFieldType.STRING).description("업데이트할 유저의 이메일. null로 설정 시, 업데이트 하지 않음.").optional(),
                                                fieldWithPath("phone").type(JsonFieldType.STRING).description("업데이트할 유저의 전화번호. null로 설정 시, 업데이트 하지 않음. Blank로 요청시, 기존 정보를 삭제.").optional()
                                        )
                                        .build()
                        )
                ));

        verify(userService).updateUser(anyLong(), any(UserUpdateRequestDto.class));
    }

    @Test
    void updateUser_Failure_User_Is_Not_Exists() throws Exception {
        assertTrue(Mockito.mockingDetails(userService).isSpy());

        final UserUpdateRequestDto userUpdateRequestDto = UserUpdateRequestDto.builder()
                .name(null)
                .email(null)
                .phone("")
                .build();

        doReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .when(userService).updateUser(anyLong(), any(UserUpdateRequestDto.class));

        mockMvc.perform(put("/api/users/{id}", 1)
                        .content(objectMapper.writeValueAsString(userUpdateRequestDto))
                        .contentType("application/json")
                )
                .andExpect(status().isNotFound())
                .andDo(document("update-user-fail-user-is-not-exists"
                ));

        verify(userService).updateUser(anyLong(), any(UserUpdateRequestDto.class));
    }

    @Test
    void updateUser_Failure_Email_Is_Duplicated() throws Exception {
        assertTrue(Mockito.mockingDetails(userService).isSpy());

        final UserUpdateRequestDto userUpdateRequestDto = UserUpdateRequestDto.builder()
                .name(null)
                .email("a@test.com")
                .phone("")
                .build();

        doReturn(ResponseEntity.status(HttpStatus.CONFLICT).build())
                .when(userService).updateUser(anyLong(), any(UserUpdateRequestDto.class));

        mockMvc.perform(put("/api/users/{id}", 1)
                        .content(objectMapper.writeValueAsString(userUpdateRequestDto))
                        .contentType("application/json")
                )
                .andExpect(status().isConflict())
                .andDo(document("update-user-fail-email-is-duplicated"));

        verify(userService).updateUser(anyLong(), any(UserUpdateRequestDto.class));
    }

    @Test
    void updateUser_Failure_Bad_Request() throws Exception {
        assertTrue(Mockito.mockingDetails(userService).isSpy());

        final UserUpdateRequestDto userUpdateRequestDto = UserUpdateRequestDto.builder()
                .name("")
                .email("")
                .phone("010-1234-5678")
                .build();

        doReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build())
                .when(userService).updateUser(anyLong(), any(UserUpdateRequestDto.class));

        mockMvc.perform(put("/api/users/{id}", 1)
                        .content(objectMapper.writeValueAsString(userUpdateRequestDto))
                        .contentType("application/json")
                )
                .andExpect(status().isBadRequest())
                .andDo(document("update-user-fail-bad-request"
                ));
    }
}
