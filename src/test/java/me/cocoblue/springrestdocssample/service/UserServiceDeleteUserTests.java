package me.cocoblue.springrestdocssample.service;

import me.cocoblue.springrestdocssample.domain.UserEntity;
import me.cocoblue.springrestdocssample.domain.UserRepository;
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
class UserServiceDeleteUserTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testDeleteUserFound() {
        // given: 존재하는 사용자
        Long userId = 1L;
        UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(userEntity));

        // when: deleteUser 호출
        ResponseEntity<?> response = userService.deleteUser(userId);

        // then: HTTP 204, 성공 메시지 및 delete 호출 검증
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "삭제 성공 시 HTTP 204이어야 합니다.");

        verify(userRepository).delete(userEntity);
    }

    @Test
    void testDeleteUserNotFound() {
        // given: 사용자 없음
        Long userId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // when: deleteUser 호출
        ResponseEntity<?> response = userService.deleteUser(userId);

        // then: HTTP 404 Not Found 와 body가 null임을 검증
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "삭제 대상이 없으면 HTTP 404");
        assertNull(response.getBody());
    }
}
