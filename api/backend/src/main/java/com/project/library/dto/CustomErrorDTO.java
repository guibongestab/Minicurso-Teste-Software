package com.project.library.dto;

import java.time.Instant;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record CustomErrorDTO(
        @Schema(description = "Timestamp of when the error occurred") Instant timestamp,
        @Schema(description = "HTTP status code of the error") Integer status,
        @Schema(description = "Error message") String message,
        @Schema(description = "Path of the request") String path,
        @Schema(description = "List of field errors (if applicable)") List<FieldError> fieldErrors) {

    public record FieldError(
            @Schema(description = "Name of the field with the error") String field,
            @Schema(description = "Error message for the field") String message) {
    }
}