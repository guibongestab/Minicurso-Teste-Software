package com.project.library.dto;

import java.time.LocalDate;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

public record ResponseBookDTO(
        @Schema(description = "Unique identifier of the book") UUID id,
        @Schema(description = "Title of the book") String title,
        @Schema(description = "Author of the book") String author,
        @Schema(description = "Publication date of the book") LocalDate publishedDate) {
}