package me.cocoblue.springrestdocssample.controller;


import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@ActiveProfiles("test")
class UserApiControllerDeleteUserTests {
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
    void deleteUser_Success() throws Exception {
        assertTrue(Mockito.mockingDetails(userService).isSpy());

        doReturn(ResponseEntity.noContent().build())
                .when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/api/users/{id}", 1)
                        .contentType("application/json")
                )
                .andExpect(status().isNoContent())
                .andDo(document("delete-user",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .summary("User를 삭제하는 Endpoint")
                                        .description("Path에 삭제할 User의 Id를 지정하여 호출합니다.")
                                        .requestSchema(Schema.schema("UserDeleteRequest"))
                                        .responseSchema(Schema.schema("UserDeleteResponse"))
                                        .pathParameters(
                                                parameterWithName("id").description("삭제할 User의 id")
                                        )
                                        .build()
                        )
                ));

        verify(userService).deleteUser(anyLong());
    }

    @Test
    void deleteUser_Failure_User_Is_Not_Exists() throws Exception {
        assertTrue(Mockito.mockingDetails(userService).isSpy());

        doReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build())
                .when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/api/users/{id}", 1)
                        .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andDo(document("delete-users-fail-user-is-not-exists"));

        verify(userService).deleteUser(anyLong());
    }
}
