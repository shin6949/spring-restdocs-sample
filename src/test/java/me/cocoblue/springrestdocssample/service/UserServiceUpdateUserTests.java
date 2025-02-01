package me.cocoblue.springrestdocssample.service;

import me.cocoblue.springrestdocssample.domain.UserEntity;
import me.cocoblue.springrestdocssample.domain.UserRepository;
import me.cocoblue.springrestdocssample.dto.UserUpdateRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceUpdateUserTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testUpdateUserFound() {
        // given: 기존 UserEntity 생성 (업데이트 전 값)
        Long userId = 1L;
        UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .name("OldName")
                .email("old@example.com")
                .phone("010-1234-5678")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        // 업데이트를 위한 DTO 생성 (각 필드에 신규 값)
        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto();
        updateRequest.setName("NewName");
        updateRequest.setEmail("new@example.com");
        updateRequest.setPhone("  010-1111-2222  "); // 양쪽 공백 포함

        // when: 업데이트 호출
        ResponseEntity<?> response = userService.updateUser(userId, updateRequest);

        // then: HTTP 204 NO_CONTENT 반환 확인
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "사용자 업데이트 성공 시 HTTP 204");

        // 실제 Entity의 값이 DTO에 따라 수정되었는지 검증 (공백은 trim 처리됨)
        assertEquals("NewName", userEntity.getName());
        assertEquals("new@example.com", userEntity.getEmail());
        assertEquals("010-1111-2222", userEntity.getPhone());

        // save()가 호출되었는지 검증
        verify(userRepository).save(userEntity);
    }

    @Test
    void testUpdateUserNotFound() {
        // given: 존재하지 않는 사용자
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto();
        updateRequest.setName("NewName");
        updateRequest.setEmail("new@example.com");
        updateRequest.setPhone("anyPhone");

        // when: 업데이트 호출
        ResponseEntity<?> response = userService.updateUser(userId, updateRequest);

        // then: 사용자가 없을 경우 HTTP 404 Not Found 반환 및 body는 null이어야 함
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
