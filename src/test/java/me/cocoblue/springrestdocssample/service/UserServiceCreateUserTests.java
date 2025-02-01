package me.cocoblue.springrestdocssample.service;

import me.cocoblue.springrestdocssample.domain.UserEntity;
import me.cocoblue.springrestdocssample.domain.UserRepository;
import me.cocoblue.springrestdocssample.dto.UserCreateRequestDto;
import me.cocoblue.springrestdocssample.dto.UserCreateResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceCreateUserTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testCreateUserSuccess() {
        // given: create request dto 준비
        UserCreateRequestDto requestDto = UserCreateRequestDto.builder()
                .name("TestUser")
                .email("test@example.com")
                .phone("010-1234-5678")
                .build();

        // requestDto.toEntity() 호출 시 생성될 수 있는 UserEntity 준비
        UserEntity unsavedUserEntity = requestDto.toEntity();

        // Repository의 save()에 대한 stub:
        // 정상적인 저장 시 id와 createdAt이 있는 UserEntity 반환
        UserEntity savedUserEntity = UserEntity.builder()
                .id(1L)
                .name(unsavedUserEntity.getName())
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUserEntity);

        // when
        ResponseEntity<UserCreateResponseDto> responseEntity = userService.createUser(requestDto);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "정상 생성 시 HTTP 200 반환");
        UserCreateResponseDto responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(savedUserEntity.getId(), responseBody.getId(), "저장된 id가 응답에 포함되어야 함");
        assertEquals(savedUserEntity.getCreatedAt(), responseBody.getCreatedAt(),
                "생성 시간이 올바르게 매핑되어야 함");
    }

    @Test
    void testCreateUserFailure_whenIdIsNull() {
        // given: 요청 DTO 생성
        UserCreateRequestDto requestDto = UserCreateRequestDto.builder()
                .name("TestUser")
                .email("test@example.com")
                .build();

        // Repository의 save()가 id가 없는 UserEntity를 반환하는 경우
        UserEntity savedUserEntity = UserEntity.builder()
                .name(requestDto.getName())
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUserEntity);

        // when
        ResponseEntity<UserCreateResponseDto> responseEntity = userService.createUser(requestDto);

        // then: 내부 서버 오류 상태를 반환해야 함
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode(),
                "id가 null인 경우 HTTP 500 반환");
        assertNull(responseEntity.getBody(), "오류 발생 시 body는 null이어야 함");
    }
}
