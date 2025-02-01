package me.cocoblue.springrestdocssample.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import me.cocoblue.springrestdocssample.domain.UserEntity;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCreateRequestDto {

    @NotNull(message = "Name must not be null")
    @NotEmpty(message = "Name must not be empty")
    private String name;

    @NotNull(message = "Name must not be null")
    @NotEmpty(message = "Email must not be empty")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(
            regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$",
            message = "Invalid Korean phone number (e.g., 02-123-4567)"
    )
    private String phone;

    @Builder
    public UserCreateRequestDto(String name, String email, @Nullable String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public UserEntity toEntity() {
        return UserEntity.builder()
                .name(name != null ? name.trim() : null)
                .email(email != null ? email.toLowerCase() : null)
                .phone(phone != null ? phone.replace("-", "") : null)
                .build();
    }
}
