package me.sergejkasper.bibelbibliothek.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import me.sergejkasper.bibelbibliothek.web.views.Views;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Borrower.
 */
@Entity
@Table(name = "borrower")
@Document(indexName = "borrower")
public class Borrower implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.Public.class)
    private Long id;

    @NotNull
    @Size(max = 40)
    @Column(name = "name", length = 40, nullable = false)
    @JsonView(Views.Public.class)
    private String name;

    @Column(name = "email")
    @JsonView(Views.Public.class)
    private String email;

    @Column(name = "phone_number")
    @JsonView(Views.Public.class)
    private String phoneNumber;

    @OneToMany(mappedBy = "borrower", fetch = FetchType.EAGER)
    @JsonView(Views.Borrower.class)
    private Set<HasBook> books = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<HasBook> getBooks() {
        return books;
    }

    public void setBooks(Set<HasBook> hasBooks) {
        this.books = hasBooks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Borrower borrower = (Borrower) o;
        if(borrower.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, borrower.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Borrower{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", email='" + email + "'" +
            ", phoneNumber='" + phoneNumber + "'" +
            '}';
    }
}
