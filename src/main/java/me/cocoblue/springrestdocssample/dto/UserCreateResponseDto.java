package me.cocoblue.springrestdocssample.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserCreateResponseDto {
    @Builder
    public UserCreateResponseDto(Long id, LocalDateTime createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    private Long id;
    private LocalDateTime createdAt;
}
