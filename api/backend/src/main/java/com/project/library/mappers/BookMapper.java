package com.project.library.mappers;

import org.springframework.stereotype.Component;

import com.project.library.dto.RequestBookDTO;
import com.project.library.dto.ResponseBookDTO;
import com.project.library.entities.Book;

@Component
public class BookMapper {

    public ResponseBookDTO toResponseDTO(Book book) {
        return new ResponseBookDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublishedDate());
    }

    public Book toEntity(RequestBookDTO bookDTO) {
        return new Book(
                null,
                bookDTO.title(),
                bookDTO.author(),
                bookDTO.publishedDate());

    }
}