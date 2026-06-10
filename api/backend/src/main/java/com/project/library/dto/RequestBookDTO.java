package com.project.library.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RequestBookDTO(
        @Schema(description = "Title of the book") @NotBlank(message = "Title must not be empty") @Size(min = 2, max = 50, message = "Title must be between 2 and 100 characters") String title,
        @Schema(description = "Author of the book") @Size(max = 100, message = "Author must be between 2 and 100 characters") String author,
        @Schema(description = "Publication date of the book") LocalDate publishedDate) {
}