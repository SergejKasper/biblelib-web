package me.sergejkasper.bibelbibliothek.repository;

import me.sergejkasper.bibelbibliothek.domain.HasBook;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the HasBook entity.
 */
@SuppressWarnings("unused")
public interface HasBookRepository extends JpaRepository<HasBook,Long> {

    @Query("select hasBook from HasBook hasBook left join fetch hasBook.borrowers left join fetch hasBook.books")
    List<HasBook> findAllWithEagerRelationships();

    @Query("select hasBook from HasBook hasBook left join fetch hasBook.borrowers left join fetch hasBook.books where hasBook.id =:id")
    HasBook findOneWithEagerRelationships(@Param("id") Long id);

}
