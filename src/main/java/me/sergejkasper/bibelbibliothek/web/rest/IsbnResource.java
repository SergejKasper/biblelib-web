package me.sergejkasper.bibelbibliothek.web.rest;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.codahale.metrics.annotation.Timed;
import me.sergejkasper.bibelbibliothek.web.rest.dto.LoggerDTO;
import me.sergejkasper.bibelbibliothek.web.websocket.dto.IsbnDTO;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

/**
 * Created by Sergej on 19.08.16.
 */
@RestController
@RequestMapping("/api")
public class IsbnResource {

    @Inject
    SimpMessageSendingOperations messagingTemplate;

    @RequestMapping(value = "/isbn",
        method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Timed
    public void newBookIsbn(@RequestBody IsbnDTO isbnDTO) {
        messagingTemplate.convertAndSend("/topic/tracker", isbnDTO);
    }
}
