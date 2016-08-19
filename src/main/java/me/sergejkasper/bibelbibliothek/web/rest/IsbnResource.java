package me.sergejkasper.bibelbibliothek.web.rest;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.codahale.metrics.annotation.Timed;
import me.sergejkasper.bibelbibliothek.domain.Book;
import me.sergejkasper.bibelbibliothek.domain.HasBook;
import me.sergejkasper.bibelbibliothek.repository.BookRepository;
import me.sergejkasper.bibelbibliothek.repository.HasBookRepository;
import me.sergejkasper.bibelbibliothek.web.rest.dto.LoggerDTO;
import me.sergejkasper.bibelbibliothek.web.websocket.dto.IsbnDTO;
import org.hibernate.criterion.Restrictions;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.util.function.Consumer;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Created by Sergej on 19.08.16.
 */
@RestController
@RequestMapping("/api")
public class IsbnResource {

    @Inject
    SimpMessageSendingOperations messagingTemplate;

    @Inject
    private BookRepository bookRepository;

    @Inject
    private HasBookRepository hasBookRepository;


    @RequestMapping(value = "/isbn",
        method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Timed
    @SendTo("/topic/isbn")
    public void newBookIsbn(@RequestBody IsbnDTO isbnDTO) {
        if(isbnDTO.getAction().equals(IsbnDTO.Action.BORROW)) {

        }
        if(isbnDTO.getAction().equals(IsbnDTO.Action.RETURN)) {
            HasBookResource hbr = new HasBookResource();
            bookRepository.findOneByBookIsbn(isbnDTO.getIsbn()).ifPresent(new Consumer<Book>() {
                @Override
                public void accept(Book book) {
                    hasBookRepository.delete(book.getBorrowers());
                }
            });
        }
        messagingTemplate.convertAndSend("/topic/isbn", isbnDTO);
    }
}
