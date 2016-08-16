package me.sergejkasper.bibelbibliothek.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.elasticsearch.annotations.Document;
import javax.persistence.FetchType;
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
    private Long id;

    @Column(name = "book_isbn")
    private Long bookIsbn;

    @NotNull
    @Size(max = 80)
    @Column(name = "title", length = 80, nullable = false)
    private String title;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private Language language;

    @Lob
    @Column(name = "cover")
    private byte[] cover;

    @Column(name = "cover_content_type")
    private String coverContentType;

    @ManyToOne
    private Author author;

    @ManyToMany(mappedBy = "books", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<HasBook> borrowers = new HashSet<>();

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

    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    public String getCoverContentType() {
        return coverContentType;
    }

    public void setCoverContentType(String coverContentType) {
        this.coverContentType = coverContentType;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Set<HasBook> getBorrowers() {
        return borrowers;
    }

    public void setBorrowers(Set<HasBook> hasBooks) {
        this.borrowers = hasBooks;
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
            ", cover='" + cover + "'" +
            ", coverContentType='" + coverContentType + "'" +
            '}';
    }
}
