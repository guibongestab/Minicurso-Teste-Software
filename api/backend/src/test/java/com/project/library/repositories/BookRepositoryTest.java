package com.project.library.repositories;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.project.library.entities.Book;
import com.project.library.factory.BookFactory;

@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    private Book newBook, savedBook;
    private UUID invalidId;
    private Pageable pageable;

    @BeforeEach
    public void setUp() {
        newBook = BookFactory.createBook();
        invalidId = UUID.randomUUID();
        pageable = PageRequest.of(0, 10);

        savedBook = bookRepository.save(newBook);
    }

    @Test
    @DisplayName("save - should persist a new book when entity does not exist")
    public void saveShouldPersistBookWhenEntityDoesNotExist() {
        Assertions.assertThat(savedBook).isNotNull();
        Assertions.assertThat(savedBook.getId()).isNotNull();
        Assertions.assertThat(savedBook.getTitle()).isEqualTo(newBook.getTitle());
        Assertions.assertThat(savedBook.getAuthor()).isEqualTo(newBook.getAuthor());
        Assertions.assertThat(savedBook.getPublishedDate()).isEqualTo(newBook.getPublishedDate());
    }

    @Test
    @DisplayName("save - should update a book when entity exists")
    public void saveShouldUpdateBookWhenEntityExists() {
        String updatedTitle = "Updated Title";
        String updatedAuthor = "Updated Author";
        LocalDate updatedPublishedDate = savedBook.getPublishedDate().plusDays(1);

        savedBook.setTitle(updatedTitle);
        savedBook.setAuthor(updatedAuthor);
        savedBook.setPublishedDate(updatedPublishedDate);

        Book updatedBook = bookRepository.save(savedBook);

        Assertions.assertThat(updatedBook).isNotNull();
        Assertions.assertThat(updatedBook.getId()).isEqualTo(savedBook.getId());
        Assertions.assertThat(updatedBook.getTitle()).isEqualTo(updatedTitle);
        Assertions.assertThat(updatedBook.getAuthor()).isEqualTo(updatedAuthor);
        Assertions.assertThat(updatedBook.getPublishedDate()).isEqualTo(updatedPublishedDate);
    }

    @Test
    @DisplayName("findById - should return book when valid id is provided")
    public void findByIdShouldReturnBookWhenValidIdIsProvided() {
        Optional<Book> foundBook = bookRepository.findById(savedBook.getId());

        Assertions.assertThat(foundBook).isPresent();
        Assertions.assertThat(foundBook.get().getId()).isEqualTo(savedBook.getId());
        Assertions.assertThat(foundBook.get().getTitle()).isEqualTo(savedBook.getTitle());
        Assertions.assertThat(foundBook.get().getAuthor()).isEqualTo(savedBook.getAuthor());
        Assertions.assertThat(foundBook.get().getPublishedDate()).isEqualTo(savedBook.getPublishedDate());
    }

    @Test
    @DisplayName("findById - should return empty when invalid id is provided")
    public void findByIdShouldReturnEmptyWhenInvalidIdIsProvided() {
        Optional<Book> foundBook = bookRepository.findById(invalidId);

        Assertions.assertThat(foundBook).isEmpty();
    }

    @Test
    @DisplayName("findAll - should return page of books")
    public void findAllShouldReturnPageOfBooks() {
        Page<Book> foundBooks = bookRepository.findAll(pageable);

        Assertions.assertThat(foundBooks).isNotNull();
        Assertions.assertThat(foundBooks.getContent()).isNotEmpty();
        Assertions.assertThat(foundBooks.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(foundBooks.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("findAll - should return empty page of books when no books exist")
    public void findAllShouldReturnEmptyPageOfBooksWhenNoBooksExist() {
        bookRepository.deleteAll();

        Page<Book> foundBooks = bookRepository.findAll(pageable);

        Assertions.assertThat(foundBooks).isNotNull();
        Assertions.assertThat(foundBooks.getContent()).isEmpty();
        Assertions.assertThat(foundBooks.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(foundBooks.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("delete - should remove book")
    public void deleteShouldRemoveBook() {
        bookRepository.delete(savedBook);

        Optional<Book> foundBook = bookRepository.findById(savedBook.getId());

        Assertions.assertThat(foundBook).isEmpty();
    }
}