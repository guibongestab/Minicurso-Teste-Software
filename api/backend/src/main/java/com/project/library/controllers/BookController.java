package com.project.library.controllers;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.RequestBody;

import com.project.library.services.BookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import com.project.library.dto.CustomErrorDTO;
import com.project.library.dto.RequestBookDTO;
import com.project.library.dto.ResponseBookDTO;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/books")
@Tag(name = "Books", description = "Endpoints for managing books in the library")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Register a new book", description = "Creates a new book in the library")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book created", content = @Content(schema = @Schema(implementation = ResponseBookDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = CustomErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content(schema = @Schema(implementation = CustomErrorDTO.class)))
    })
    @PostMapping(produces = "application/json")
    public ResponseEntity<ResponseBookDTO> registerBook(@RequestBody @Valid RequestBookDTO newBook) {
        ResponseBookDTO result = bookService.createBook(newBook);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{uuid}")
                .buildAndExpand(result.id())
                .toUri();
        return ResponseEntity.created(uri).body(result);
    }

    @Operation(summary = "Get book by ID", description = "Retrieves a book by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found", content = @Content(schema = @Schema(implementation = ResponseBookDTO.class))),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content(schema = @Schema(implementation = CustomErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content(schema = @Schema(implementation = CustomErrorDTO.class)))
    })
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<ResponseBookDTO> getBookById(
            @Parameter(description = "The unique identifier of the book") @PathVariable UUID id) {
        ResponseBookDTO result = bookService.getBookById(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get all books", description = "Retrieves a paginated list of all books in the library")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books retrieved"),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content(schema = @Schema(implementation = CustomErrorDTO.class)))
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<Page<ResponseBookDTO>> getAllBooks(Pageable pageable) {
        Page<ResponseBookDTO> result = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update a book", description = "Updates the details of an existing book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated", content = @Content(schema = @Schema(implementation = ResponseBookDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = CustomErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content(schema = @Schema(implementation = CustomErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content(schema = @Schema(implementation = CustomErrorDTO.class)))
    })
    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<ResponseBookDTO> updateBook(
            @Parameter(description = "The unique identifier of the book") @PathVariable UUID id,
            @RequestBody @Valid RequestBookDTO bookDto) {
        ResponseBookDTO result = bookService.updateBook(id, bookDto);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Delete a book", description = "Deletes a book from the library")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book deleted"),
            @ApiResponse(responseCode = "404", description = "Book not found", content = @Content(schema = @Schema(implementation = CustomErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content(schema = @Schema(implementation = CustomErrorDTO.class)))
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "The unique identifier of the book") @PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
