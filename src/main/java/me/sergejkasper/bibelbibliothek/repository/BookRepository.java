package me.sergejkasper.bibelbibliothek.repository;

import me.sergejkasper.bibelbibliothek.domain.Book;

import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Book entity.
 */
@SuppressWarnings("unused")
public interface BookRepository extends JpaRepository<Book,Long> {
    Optional<Book> findOneByBookIsbn(String isbn);
}
