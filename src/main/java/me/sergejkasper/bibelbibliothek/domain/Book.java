package me.sergejkasper.bibelbibliothek.domain;

import com.fasterxml.jackson.annotation.*;
import me.sergejkasper.bibelbibliothek.web.views.Views;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import me.sergejkasper.bibelbibliothek.domain.enumeration.Language;

/**
 * A Book.
 */
@Entity
@Table(name = "book")
@Document(indexName = "book")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.Public.class)
    private Long id;

    @Column(name = "book_isbn")
    @JsonView(Views.Public.class)
    private Long bookIsbn;

    @NotNull
    @Size(max = 80)
    @Column(name = "title", length = 80, nullable = false)
    @JsonView(Views.Public.class)
    private String title;


    @Lob
    @Column(name = "description")
    @JsonView(Views.Public.class)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    @JsonView(Views.Public.class)
    private Language language;

    @Lob
    @Column(name = "cover")
    @JsonView(Views.Public.class)
    private String cover;

    @Column(name = "cover_content_type")
    @JsonView(Views.Public.class)
    private String coverContentType;

    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER)
    @JsonView(Views.Book.class)
    private Set<HasBook> borrowers = new HashSet<HasBook>();

    @ManyToOne
    @JsonView(Views.Public.class)
    private Author author;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(Long bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCoverContentType() {
        return coverContentType;
    }

    public void setCoverContentType(String coverContentType) {
        this.coverContentType = coverContentType;
    }

    public Set<HasBook> getBorrowers() {
        return borrowers;
    }

    public void setBorrowers(Set<HasBook> hasBooks) {
        this.borrowers = hasBooks;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Book book = (Book) o;
        if(book.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Book{" +
            "id=" + id +
            ", bookIsbn='" + bookIsbn + "'" +
            ", title='" + title + "'" +
            ", description='" + description + "'" +
            ", language='" + language + "'" +
            ", coverContentType='" + coverContentType + "'" +
            '}';
    }
}
