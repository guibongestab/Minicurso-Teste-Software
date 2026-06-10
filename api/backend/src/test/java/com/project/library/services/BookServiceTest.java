package com.project.library.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.project.library.dto.RequestBookDTO;
import com.project.library.dto.ResponseBookDTO;
import com.project.library.entities.Book;
import com.project.library.exceptions.EntityNotFoundException;
import com.project.library.factory.BookFactory;
import com.project.library.mappers.BookMapper;
import com.project.library.repositories.BookRepository;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    private Book book;
    private ResponseBookDTO responseBookDTO;
    private RequestBookDTO requestBookDTO;
    private UUID usedId, unusedId;
    private PageImpl<Book> bookPage;
    private Pageable pageable;

    @BeforeEach
    public void setup() {
        usedId = UUID.randomUUID();
        unusedId = UUID.randomUUID();

        book = BookFactory.createBook(usedId);
        responseBookDTO = BookFactory.createResponseBookDTO(usedId);
        requestBookDTO = BookFactory.createRequestBookDTO();

        pageable = PageRequest.of(0, 10);
        bookPage = new PageImpl<>(List.of(book), pageable, 1);
    }

    @Test
    @DisplayName("createBook - should return ResponseBookDTO when book is created")
    public void createBookShouldReturnResponseBookDTOWhenBookIsCreated() {
        Mockito.when(bookMapper.toEntity(Mockito.any(RequestBookDTO.class))).thenReturn(book);
        Mockito.when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book);
        Mockito.when(bookMapper.toResponseDTO(Mockito.any(Book.class))).thenReturn(responseBookDTO);

        ResponseBookDTO result = bookService.createBook(requestBookDTO);

        Mockito.verify(bookMapper).toEntity(requestBookDTO);
        Mockito.verify(bookRepository).save(book);
        Mockito.verify(bookMapper).toResponseDTO(book);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(usedId);
        Assertions.assertThat(result.title()).isEqualTo(requestBookDTO.title());
        Assertions.assertThat(result.author()).isEqualTo(requestBookDTO.author());
        Assertions.assertThat(result.publishedDate()).isEqualTo(requestBookDTO.publishedDate());
    }

    @Test
    @DisplayName("getBookById - should return ResponseBookDTO when book is found")
    public void getBookByIdShouldReturnResponseBookDTOWhenBookIsFound() {
        Mockito.when(bookRepository.findById(usedId)).thenReturn(Optional.of(book));
        Mockito.when(bookMapper.toResponseDTO(Mockito.any(Book.class))).thenReturn(responseBookDTO);

        ResponseBookDTO result = bookService.getBookById(usedId);

        Mockito.verify(bookRepository).findById(usedId);
        Mockito.verify(bookMapper).toResponseDTO(book);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(usedId);
        Assertions.assertThat(result.title()).isEqualTo(requestBookDTO.title());
        Assertions.assertThat(result.author()).isEqualTo(requestBookDTO.author());
        Assertions.assertThat(result.publishedDate()).isEqualTo(requestBookDTO.publishedDate());
    }

    @Test
    @DisplayName("getBookById - should throw EntityNotFoundException when book is not found")
    public void getBookByIdShouldThrowExceptionWhenBookNotFound() {
        Mockito.when(bookRepository.findById(unusedId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> bookService.getBookById(unusedId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("getAllBooks - should return page of ResponseBookDTO")
    public void getAllBooksShouldReturnPageOfResponseBookDTO() {
        Mockito.when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        Mockito.when(bookMapper.toResponseDTO(Mockito.any(Book.class))).thenReturn(responseBookDTO);

        Page<ResponseBookDTO> result = bookService.getAllBooks(pageable);

        Mockito.verify(bookRepository).findAll(pageable);
        Mockito.verify(bookMapper).toResponseDTO(book);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("updateBook - should return updated ResponseBookDTO")
    public void updateBookShouldReturnUpdatedResponseBookDTO() {
        Mockito.when(bookRepository.findById(usedId)).thenReturn(Optional.of(book));
        Mockito.when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book);
        Mockito.when(bookMapper.toResponseDTO(Mockito.any(Book.class))).thenReturn(responseBookDTO);

        ResponseBookDTO result = bookService.updateBook(usedId, requestBookDTO);

        Mockito.verify(bookRepository).findById(usedId);
        Mockito.verify(bookRepository).save(book);
        Mockito.verify(bookMapper).toResponseDTO(book);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(usedId);
        Assertions.assertThat(result.title()).isEqualTo(requestBookDTO.title());
        Assertions.assertThat(result.author()).isEqualTo(requestBookDTO.author());
        Assertions.assertThat(result.publishedDate()).isEqualTo(requestBookDTO.publishedDate());
    }

    @Test
    @DisplayName("updateBook - should throw EntityNotFoundException when book is not found")
    public void updateBookShouldThrowExceptionWhenBookNotFound() {
        Mockito.when(bookRepository.findById(unusedId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> bookService.updateBook(unusedId, requestBookDTO))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("deleteBook - should delete book")
    public void deleteBookShouldDeleteBook() {
        Mockito.when(bookRepository.findById(usedId)).thenReturn(Optional.of(book));

        Assertions.assertThatCode(() -> bookService.deleteBook(usedId))
                .doesNotThrowAnyException();

        Mockito.verify(bookRepository).findById(usedId);
        Mockito.verify(bookRepository).delete(book);
    }

    @Test
    @DisplayName("deleteBook - should throw EntityNotFoundException when book is not found")
    public void deleteBookShouldThrowExceptionWhenBookNotFound() {
        Mockito.when(bookRepository.findById(unusedId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> bookService.deleteBook(unusedId))
                .isInstanceOf(EntityNotFoundException.class);
    }
}