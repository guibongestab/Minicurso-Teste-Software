package com.project.library.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.library.dto.RequestBookDTO;
import com.project.library.dto.ResponseBookDTO;
import com.project.library.entities.Book;
import com.project.library.exceptions.EntityNotFoundException;
import com.project.library.mappers.BookMapper;
import com.project.library.repositories.BookRepository;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Transactional
    public ResponseBookDTO createBook(RequestBookDTO bookDto) {
        Book bookEntity = bookMapper.toEntity(bookDto);
        Book savedBook = bookRepository.save(bookEntity);

        return bookMapper.toResponseDTO(savedBook);
    }

    public ResponseBookDTO getBookById(UUID id) {
        ResponseBookDTO result = bookMapper.toResponseDTO(getBookEntityById(id));
        return result;
    }

    public Page<ResponseBookDTO> getAllBooks(Pageable pageable) {
        Page<Book> pagedBooks = bookRepository.findAll(pageable);

        return pagedBooks.map(bookMapper::toResponseDTO);
    }

    @Transactional
    public ResponseBookDTO updateBook(UUID id, RequestBookDTO bookDto) {
        Book existingBook = getBookEntityById(id);

        existingBook.setTitle(bookDto.title());
        existingBook.setAuthor(bookDto.author());
        existingBook.setPublishedDate(bookDto.publishedDate());

        Book savedBook = bookRepository.save(existingBook);

        return bookMapper.toResponseDTO(savedBook);
    }

    @Transactional
    public void deleteBook(UUID id) {
        Book book = getBookEntityById(id);

        bookRepository.delete(book);
    }

    private Book getBookEntityById(UUID id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found!"));
    }
}
