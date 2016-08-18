package me.sergejkasper.bibelbibliothek.domain;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import me.sergejkasper.bibelbibliothek.web.views.Views;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
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
    @JsonView(Views.Public.class)
    private Long id;

    @NotNull
    @Column(name = "borrow_date", nullable = false)
    @JsonView(Views.Public.class)
    private LocalDate borrowDate;

    @NotNull
    @Column(name = "return_date", nullable = false)
    @JsonView(Views.Public.class)
    private LocalDate returnDate;

    @ManyToOne
    @JsonView(Views.HasBook.class)
    private Borrower borrower;

    @ManyToOne
    @JsonView(Views.HasBook.class)
    private Book book;

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

    public Borrower getBorrower() {
        return borrower;
    }

    public void setBorrower(Borrower borrower) {
        this.borrower = borrower;
    }


    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
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
            '}';
    }
}
