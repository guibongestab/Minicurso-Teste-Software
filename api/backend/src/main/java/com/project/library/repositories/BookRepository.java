package com.project.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.library.entities.Book;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
}