package me.cocoblue.springrestdocssample.service;

import me.cocoblue.springrestdocssample.domain.UserEntity;
import me.cocoblue.springrestdocssample.domain.UserRepository;
import me.cocoblue.springrestdocssample.dto.UserResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceGetUserTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetUserFound() {
        // given
        Long userId = 1L;
        UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .name("TestUser")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        // when
        ResponseEntity<UserResponseDto> response = userService.getUser(userId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode(), "사용자 존재 시 HTTP 200이어야 함");
        UserResponseDto dto = response.getBody();
        assertNotNull(dto, "응답 body는 null이 아니어야 합니다.");
        assertEquals(userEntity.getId(), dto.getId());
    }

    @Test
    void testGetUserNotFound() {
        // given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        ResponseEntity<UserResponseDto> response = userService.getUser(userId);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "사용자 미존재 시 HTTP 404가 반환되어야 함");
        assertNull(response.getBody(), "사용자 없을 때 body는 null이어야 합니다.");
    }
}
