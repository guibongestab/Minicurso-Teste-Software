package com.project.library.factory;

import java.time.LocalDate;
import java.util.UUID;

import com.project.library.dto.RequestBookDTO;
import com.project.library.dto.ResponseBookDTO;
import com.project.library.entities.Book;

public class BookFactory {

    private static final String BOOK_TITLE = "The Way of Kings";
    private static final String BOOK_AUTHOR = "Brandon Sanderson";
    private static final LocalDate BOOK_PUBLISHED_DATE = LocalDate.of(2010, 8, 6);

    public static Book createBook() {
        return new Book(
                null,
                BOOK_TITLE,
                BOOK_AUTHOR,
                BOOK_PUBLISHED_DATE);
    }

    public static Book createBook(UUID bookId) {
        return new Book(
                bookId,
                BOOK_TITLE,
                BOOK_AUTHOR,
                BOOK_PUBLISHED_DATE);
    }

    public static RequestBookDTO createRequestBookDTO() {
        return new RequestBookDTO(
                BOOK_TITLE,
                BOOK_AUTHOR,
                BOOK_PUBLISHED_DATE);
    }

    public static ResponseBookDTO createResponseBookDTO() {
        return new ResponseBookDTO(
                null,
                BOOK_TITLE,
                BOOK_AUTHOR,
                BOOK_PUBLISHED_DATE);
    }

    public static ResponseBookDTO createResponseBookDTO(UUID bookId) {
        return new ResponseBookDTO(
                bookId,
                BOOK_TITLE,
                BOOK_AUTHOR,
                BOOK_PUBLISHED_DATE);
    }

    public static RequestBookDTO createRequestBookDtoWithTitle(String title) {
        return new RequestBookDTO(
                title,
                BOOK_AUTHOR,
                BOOK_PUBLISHED_DATE);
    }

    public static RequestBookDTO createRequestBookDtoWithAuthor(String author) {
        return new RequestBookDTO(
                BOOK_TITLE,
                author,
                BOOK_PUBLISHED_DATE);
    }

    public static RequestBookDTO createRequestBookDto(String title, String author, LocalDate publishedDate) {
        return new RequestBookDTO(
                title,
                author,
                publishedDate);
    }
}