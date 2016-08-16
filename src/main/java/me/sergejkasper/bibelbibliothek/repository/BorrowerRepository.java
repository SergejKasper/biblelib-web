package me.sergejkasper.bibelbibliothek.repository;

import me.sergejkasper.bibelbibliothek.domain.Borrower;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Borrower entity.
 */
@SuppressWarnings("unused")
public interface BorrowerRepository extends JpaRepository<Borrower,Long> {

}
