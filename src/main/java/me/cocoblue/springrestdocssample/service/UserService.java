package me.cocoblue.springrestdocssample.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.springrestdocssample.domain.UserEntity;
import me.cocoblue.springrestdocssample.domain.UserRepository;
import me.cocoblue.springrestdocssample.dto.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public ResponseEntity<PaginatedResponseDto<UserResponseDto>> getUsersList(final Long cursor, final Integer requestedSize) {
        log.debug("getUsersList() called with cursor: {}, requestedSize: {}", cursor, requestedSize);

        final int pageSize = requestedSize == null ? 10 : requestedSize;
        final Pageable pageable = PageRequest.of(0, pageSize + 1, Sort.by("id").ascending());

        final List<UserEntity> results = userRepository.findNextPage(cursor, pageable);

        final PaginatedResponseDto<UserResponseDto> response = new PaginatedResponseDto<>();
        if (results.size() > pageSize) {
            // 다음 페이지가 존재함. 실제 데이터는 앞의 size만.
            response.setData(results.subList(0, pageSize).stream().map(UserResponseDto::new).toList());
            // 다음 커서는 (requestedSize+1)번째 항목의 id 또는 마지막 항목의 id로 결정
            response.setNextCursor(results.get(pageSize).getId());
            response.setHasNext(true);
        } else {
            // 다음 페이지 없음
            response.setData(results.stream().map(UserResponseDto::new).toList());
            response.setHasNext(false);
        }

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<UserResponseDto> getUser(Long id) {
        log.debug("getUser() called with id: {}", id);

        final Optional<UserEntity> result = userRepository.findById(id);
        log.debug("User Result: {}", result);
        return result.map(userEntity -> ResponseEntity.ok(new UserResponseDto(userEntity))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<UserCreateResponseDto> createUser(final UserCreateRequestDto userCreateRequestDto) {
        log.debug("createUser() called with userRequestDto: {}", userCreateRequestDto);

        final Optional<UserEntity> existingUser = userRepository.findByEmail(userCreateRequestDto.getEmail());
        if (existingUser.isPresent()) {
            log.warn("User already exists with email: {}", userCreateRequestDto.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        UserEntity savedUser = userRepository.save(userCreateRequestDto.toEntity());
        log.debug("User saved: {}", savedUser);
        if (savedUser.getId() == null) {
            return ResponseEntity.internalServerError().build();
        }

        final UserCreateResponseDto response = UserCreateResponseDto.builder()
                .id(savedUser.getId())
                .createdAt(savedUser.getCreatedAt())
                .build();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> updateUser(Long id, UserUpdateRequestDto dto) {
        log.debug("updateUser() called with id: {}, dto: {}", id, dto);

        final Optional<UserEntity> existingUser = userRepository.findByEmail(dto.getEmail());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            log.warn("User already exists with email: {}", dto.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return userRepository.findById(id)
                .map(userEntity -> {
                    // 업데이트 로직
                    if (dto.getName() != null) {
                        userEntity.setName(dto.getName());
                    }
                    if (dto.getEmail() != null) {
                        userEntity.setEmail(dto.getEmail());
                    }
                    if (dto.getPhone() != null) {
                        userEntity.setPhone(dto.getPhone().isBlank() ? null : dto.getPhone().trim());
                    }
                    userRepository.save(userEntity);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> deleteUser(Long id) {
        log.debug("deleteUser() called with id: {}", id);

        return userRepository.findById(id)
                .map(userEntity -> {
                    userRepository.delete(userEntity);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> {
                    log.warn("User not found with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }
}
