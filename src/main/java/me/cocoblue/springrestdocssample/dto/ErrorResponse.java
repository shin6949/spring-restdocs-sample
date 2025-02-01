package me.cocoblue.springrestdocssample.dto;

import java.util.List;

public record ErrorResponse(
        int status,
        String message,
        List<String> errors
) {}