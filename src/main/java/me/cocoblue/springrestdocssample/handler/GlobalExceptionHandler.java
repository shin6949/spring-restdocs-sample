package me.cocoblue.springrestdocssample.handler;

import jakarta.validation.ConstraintViolationException;
import me.cocoblue.springrestdocssample.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationExceptions(Exception ex) {
        List<String> errors = new ArrayList<>();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (ex instanceof ConstraintViolationException cve) {
            errors = cve.getConstraintViolations().stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.toList());
        } else if (ex instanceof MethodArgumentNotValidException manve) {
            errors = manve.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
        }

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                "Validation failed",
                errors
        );

        return new ResponseEntity<>(errorResponse, status);
    }
}
