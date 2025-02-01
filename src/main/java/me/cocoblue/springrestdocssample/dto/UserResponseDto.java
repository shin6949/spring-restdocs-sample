package me.cocoblue.springrestdocssample.dto;

import lombok.*;
import me.cocoblue.springrestdocssample.domain.UserEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserResponseDto {
    @Builder
    public UserResponseDto(Long id, String name, String email, String phone, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UserResponseDto(UserEntity entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.email = entity.getEmail();
        this.phone = entity.getPhone();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }

    public UserEntity toEntity() {
        return UserEntity.builder()
                .id(id)
                .name(name)
                .email(email)
                .phone(phone)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private Long id;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
