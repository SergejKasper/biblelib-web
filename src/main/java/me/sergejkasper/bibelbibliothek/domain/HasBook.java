package me.sergejkasper.bibelbibliothek.domain;

import org.springframework.data.elasticsearch.annotations.Document;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A HasBook.
 */
@Entity
@Table(name = "has_book")
@Document(indexName = "hasbook")
public class HasBook implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;

    @NotNull
    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

    @Column(name = "returned")
    private Boolean returned;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonProperty("borrowers")
    @JoinTable(name = "has_book_borrower",
               joinColumns = @JoinColumn(name="has_books_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="borrowers_id", referencedColumnName="ID"))
    private Set<Borrower> borrowers = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonProperty("books")
    @JoinTable(name = "has_book_book",
               joinColumns = @JoinColumn(name="has_books_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="books_id", referencedColumnName="ID"))
    private Set<Book> books = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Boolean isReturned() {
        return returned;
    }

    public void setReturned(Boolean returned) {
        this.returned = returned;
    }

    public Set<Borrower> getBorrowers() {
        return borrowers;
    }

    public void setBorrowers(Set<Borrower> borrowers) {
        this.borrowers = borrowers;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HasBook hasBook = (HasBook) o;
        if(hasBook.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, hasBook.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "HasBook{" +
            "id=" + id +
            ", borrowDate='" + borrowDate + "'" +
            ", returnDate='" + returnDate + "'" +
            ", returned='" + returned + "'" +
            '}';
    }
}
