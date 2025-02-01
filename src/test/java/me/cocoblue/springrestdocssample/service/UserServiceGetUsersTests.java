package me.cocoblue.springrestdocssample.service;

import me.cocoblue.springrestdocssample.domain.UserEntity;
import me.cocoblue.springrestdocssample.domain.UserRepository;
import me.cocoblue.springrestdocssample.dto.PaginatedResponseDto;
import me.cocoblue.springrestdocssample.dto.UserResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceGetUsersTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService; // 실제 비즈니스 로직 검증

    @Test
    void testGetUsersList_withNextPage() {
        // given: 요청 크기는 10개이고, Repository는 11개의 UserEntity를 반환 (다음 페이지 존재)
        Long cursor = 0L;
        int requestedSize = 10;
        List<UserEntity> mockUsers = createMockUsers(11);

        when(userRepository.findNextPage(eq(cursor), any(Pageable.class)))
                .thenReturn(mockUsers);

        // when
        ResponseEntity<PaginatedResponseDto<UserResponseDto>> responseEntity =
                userService.getUsersList(cursor, requestedSize);
        PaginatedResponseDto<UserResponseDto> responseBody = responseEntity.getBody();

        // then
        assertNotNull(responseBody, "응답 body는 null이 아니어야 합니다.");
        // 응답 데이터는 앞의 10개만 포함되어야 함
        assertEquals(10, responseBody.getData().size(), "반환받은 사용자 수는 10개여야 합니다.");
        // 다음 페이지 존재하므로 nextCursor는 11번째 요소의 id와 같아야 함
        assertEquals(mockUsers.get(10).getId(), responseBody.getNextCursor(), "다음 커서 값이 올바르지 않습니다.");
        assertTrue(responseBody.isHasNext(), "hasNext 값은 true가 되어야 합니다.");
    }

    @Test
    void testGetUsersList_withoutNextPage() {
        // given: 요청 크기는 10개이고, Repository는 정확히 10개의 UserEntity를 반환 (다음 페이지 없음)
        Long cursor = 0L;
        int requestedSize = 10;
        List<UserEntity> mockUsers = createMockUsers(10);

        when(userRepository.findNextPage(eq(cursor), any(Pageable.class)))
                .thenReturn(mockUsers);

        // when
        ResponseEntity<PaginatedResponseDto<UserResponseDto>> responseEntity =
                userService.getUsersList(cursor, requestedSize);
        PaginatedResponseDto<UserResponseDto> responseBody = responseEntity.getBody();

        // then
        assertNotNull(responseBody, "응답 body는 null이면 안됩니다.");
        assertEquals(10, responseBody.getData().size(), "반환받은 사용자 수는 10개여야 합니다.");
        assertFalse(responseBody.isHasNext(), "hasNext 값은 false여야 합니다.");
        // nextCursor 값은 설정되지 않으므로 null이어야 합니다.
        assertNull(responseBody.getNextCursor(), "다음 커서는 null이어야 합니다.");
    }

    private List<UserEntity> createMockUsers(int count) {
        List<UserEntity> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            UserEntity user = UserEntity.builder()
                    .id((long) (i + 1))
                    .name("TestUser" + (i + 1))
                    .email("test" + (i + 1) + "@test.com")
                    .build();

            users.add(user);
        }
        return users;
    }
}
