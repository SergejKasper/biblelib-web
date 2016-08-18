package me.sergejkasper.bibelbibliothek.web.rest.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.sergejkasper.bibelbibliothek.domain.Book;
import me.sergejkasper.bibelbibliothek.domain.Borrower;

import java.io.IOException;
import java.util.HashSet;

/**
 * Created by Sergej on 16.08.16.
 */

public class BorrowerSerializer extends StdSerializer<Borrower> {

    public BorrowerSerializer() {
        this(null);
    }

    public BorrowerSerializer(Class<Borrower> t) {
        super(t);
    }
    @Override
    public void serialize(Borrower borrower, final JsonGenerator generator,
                          final SerializerProvider provider) throws IOException, JsonProcessingException {
        borrower.setBooks(new HashSet<>());
        generator.writeObject(new Object());
    }
}

