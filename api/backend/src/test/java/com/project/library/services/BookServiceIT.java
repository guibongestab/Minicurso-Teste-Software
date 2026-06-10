package com.project.library.services;

import java.time.LocalDate;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.project.library.dto.RequestBookDTO;
import com.project.library.dto.ResponseBookDTO;
import com.project.library.exceptions.EntityNotFoundException;
import com.project.library.factory.BookFactory;

@SpringBootTest
@Transactional
public class BookServiceIT {

    @Autowired
    private BookService bookService;

    private UUID validBookId, invalidBookId;
    private RequestBookDTO requestBookDTO, updatedRequestBookDTO, newBookRequestDto;
    private ResponseBookDTO responseBookDTO;
    private Pageable pageable;

    @BeforeEach
    public void setUp() {
        requestBookDTO = BookFactory.createRequestBookDTO();
        responseBookDTO = bookService.createBook(requestBookDTO);

        validBookId = responseBookDTO.id();
        invalidBookId = UUID.randomUUID();
        pageable = PageRequest.of(0, 10);

        newBookRequestDto = BookFactory.createRequestBookDTO();
        updatedRequestBookDTO = BookFactory.createRequestBookDto("Updated Title", "Updated Author",
                LocalDate.of(2020, 1, 1));
    }

    @Test
    @DisplayName("createBook - should create and return a new book")
    public void createBookShouldReturnBookResponseDTO() {
        ResponseBookDTO result = bookService.createBook(newBookRequestDto);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isNotNull();
        Assertions.assertThat(result.title()).isEqualTo(newBookRequestDto.title());
        Assertions.assertThat(result.author()).isEqualTo(newBookRequestDto.author());
        Assertions.assertThat(result.publishedDate()).isEqualTo(newBookRequestDto.publishedDate());
    }

    @Test
    @DisplayName("getBookById - should return book response DTO when valid ID is provided")
    public void getBookByIdShouldReturnBookResponseDTOWhenValidIdIsProvided() {
        ResponseBookDTO result = bookService.getBookById(validBookId);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(validBookId);
        Assertions.assertThat(result.title()).isEqualTo(requestBookDTO.title());
        Assertions.assertThat(result.author()).isEqualTo(requestBookDTO.author());
        // Se eu tivesse um filho chamado DJ e um outro chamado SOM, quando eles estivesem brigando que pergunta eu faria?
        Assertions.assertThat(result.publishedDate()).isEqualTo(requestBookDTO.publishedDate());
    }

    @Test
    @DisplayName("getBookById - should throw EntityNotFoundException when invalid ID is provided")
    public void getBookByIdShouldThrowEntityNotFoundExceptionWhenInvalidIdIsProvided() {
        Assertions.assertThatThrownBy(() -> bookService.getBookById(invalidBookId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("getAllBooks - should return a page of book response DTOs")
    public void getAllBooksShouldReturnPageOfBookResponseDTO() {
        Page<ResponseBookDTO> result = bookService.getAllBooks(pageable);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("updateBook - should return updated book response DTO when valid ID is provided")
    public void updateBookShouldReturnUpdatedBookResponseDTOWhenValidIdIsProvided() {
        ResponseBookDTO result = bookService.updateBook(validBookId, updatedRequestBookDTO);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(validBookId);
        Assertions.assertThat(result.title()).isEqualTo(updatedRequestBookDTO.title());
        Assertions.assertThat(result.author()).isEqualTo(updatedRequestBookDTO.author());
        Assertions.assertThat(result.publishedDate()).isEqualTo(updatedRequestBookDTO.publishedDate());
    }

    @Test
    @DisplayName("updateBook - should throw EntityNotFoundException when invalid ID is provided")
    public void updateBookShouldThrowEntityNotFoundExceptionWhenInvalidIdIsProvided() {
        Assertions.assertThatThrownBy(() -> bookService.updateBook(invalidBookId, updatedRequestBookDTO))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("deleteBook - should remove book when valid ID is provided")
    public void deleteBookShouldRemoveBookWhenValidIdIsProvided() {
        Assertions.assertThatCode(() -> bookService.deleteBook(validBookId))
                .doesNotThrowAnyException();

        Assertions.assertThatThrownBy(() -> bookService.getBookById(validBookId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("deleteBook - should throw EntityNotFoundException when invalid ID is provided")
    public void deleteBookShouldThrowEntityNotFoundExceptionWhenInvalidIdIsProvided() {
        Assertions.assertThatThrownBy(() -> bookService.deleteBook(invalidBookId))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
