package me.cocoblue.springrestdocssample.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotBlankIfPresentValidator implements ConstraintValidator<NotBlankIfPresent, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 값이 null이면 검증 통과, 아니면 blank 여부 체크
        return value == null || !value.isBlank();
    }
}
