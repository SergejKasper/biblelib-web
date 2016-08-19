package me.sergejkasper.bibelbibliothek.repository;

import me.sergejkasper.bibelbibliothek.domain.HasBook;

import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the HasBook entity.
 */
@SuppressWarnings("unused")
public interface HasBookRepository extends JpaRepository<HasBook,Long> {

}
