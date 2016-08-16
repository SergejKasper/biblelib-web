package me.sergejkasper.bibelbibliothek.web.rest;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.sergejkasper.bibelbibliothek.domain.Book;
import me.sergejkasper.bibelbibliothek.domain.HasBook;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.elasticsearch.common.collect.Sets;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Sergej on 16.08.16.
 */
public class HasBookSerializer extends JsonSerializer<Set<HasBook>> {

    @Override
    public void serialize(Set<HasBook> hasBooks, final JsonGenerator generator,
                          final SerializerProvider provider) throws IOException, JsonProcessingException {
        Set<SimpleHasBook> simpleHasBooks = Sets.newHashSet();
        simpleHasBooks.addAll(hasBooks.stream().map(hasBook -> new SimpleHasBook(hasBook.getId(), hasBook.getBook().getTitle(), hasBook.getBorrower().getName())).collect(Collectors.toList()));
        generator.writeObject(simpleHasBooks);
    }

    static class SimpleHasBook implements Serializable{

        private Long id;
        private String borrower;
        private String book;

        SimpleHasBook(Long id, String book, String borrower){
            this.id = id;
            this.book = book;
            this.borrower = borrower;
        }

        public String getBook() {
            return book;
        }

        public void setBook(String book) {
            this.book = book;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "HasBook{" +
                "id=" + id +
                ", book='" + book + "'" +
                ", borrower='" + borrower + "'" +
                '}';
        }

        public void setBorrower(String borrower) {
            this.borrower = borrower;
        }
        public String getBorrower(){
            return borrower;
        }
    }

}
