package me.sergejkasper.bibelbibliothek.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import me.sergejkasper.bibelbibliothek.domain.Book;
import me.sergejkasper.bibelbibliothek.domain.Borrower;
import me.sergejkasper.bibelbibliothek.domain.HasBook;
import me.sergejkasper.bibelbibliothek.repository.BookRepository;
import me.sergejkasper.bibelbibliothek.repository.HasBookRepository;
import me.sergejkasper.bibelbibliothek.repository.search.BookSearchRepository;
import me.sergejkasper.bibelbibliothek.repository.search.HasBookSearchRepository;
import me.sergejkasper.bibelbibliothek.web.rest.util.HeaderUtil;
import me.sergejkasper.bibelbibliothek.web.rest.util.PaginationUtil;
import me.sergejkasper.bibelbibliothek.web.views.Views;
import me.sergejkasper.bibelbibliothek.web.websocket.dto.InteractionDTO;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.CommonTermsQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.MatchQueryBuilder.Operator.AND;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Created by Sergej on 19.08.16.
 */
@RestController
@RequestMapping("/socketapi")
public class IsbnResource {

    @Inject
    SimpMessageSendingOperations messagingTemplate;

    @Inject
    private HasBookSearchRepository hasBookSearchRepository;

    @Inject
    private HasBookResource hasBookResource;

    @Inject
    private HasBookRepository hasBookRepository;

    @Inject
    private BookRepository bookRepository;

    @Inject
    private BorrowerResource borrowerResource;

    @RequestMapping(value = "/interaction",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @JsonView(Views.Book.class)
    @SendTo("/topic/interaction")
    public ResponseEntity<Object> newBookIsbn(@RequestBody InteractionDTO interactionDTO) throws URISyntaxException {
        if(interactionDTO.getAction().equals(InteractionDTO.Action.BORROW)) {
            /*BoolQueryBuilder searchQueryBuilder = boolQuery().must(matchQuery("bookIsbn",interactionDTO.getIsbn()).operator(AND))
                    .must(QueryBuilders.hasChildQuery("book", matchQuery("borrowers", new HashSet<HasBook>())));*/
            Stream<Book> bookStream =StreamSupport.stream(bookRepository.findAll().spliterator(), false)
                            .filter(book -> book.getBookIsbn().toString().equals(interactionDTO.getIsbn()) && book.getBorrowers() != null && book.getBorrowers().isEmpty());
            List<Book> books = bookStream.collect(Collectors.toList());
            if(!books.isEmpty()){
                HasBook hasBook = new HasBook();
                hasBook.setBook(books.get(0));
                hasBook.setBorrower(interactionDTO.getBorrower());
                hasBook.setBorrowDate(LocalDate.now());
                hasBook.setReturnDate(LocalDate.now().plusWeeks(4));
                HasBook result = hasBookRepository.save(hasBook);
                hasBookSearchRepository.save(result);
                //ResponseEntity.created(new URI("/api/has-books/" + result.getId())).headers(HeaderUtil.createEntityCreationAlert("hasBook", result.getId().toString())).body(result)
                messagingTemplate.convertAndSend("/topic/interaction", interactionDTO);
                return new ResponseEntity<Object>("BOOK_BORROWED", HttpStatus.OK);
            }else {
                return new ResponseEntity<Object>("BOOK_NOT_BORROWED", HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
        if(interactionDTO.getAction().equals(InteractionDTO.Action.RETURN)) {
            BoolQueryBuilder searchQueryBuilder = boolQuery()
                .must(matchQuery("book.bookIsbn",interactionDTO.getIsbn()).operator(AND))
                .must(matchQuery("borrower.id",interactionDTO.getBorrower().getId()).operator(AND));
            //SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(searchQueryBuilder).withSort(new FieldSortBuilder("return_date").order(SortOrder.ASC)).build();
            //List<Book> hasBooks = StreamSupport.stream(bookSearchRepository.search(searchQuery).spliterator(), false).collect(Collectors.toList());
            Stream<HasBook> bookStream =StreamSupport.stream(hasBookRepository.findAll().spliterator(), false)
                .filter(hasBook -> hasBook.getBook().getBookIsbn().toString().equals(interactionDTO.getIsbn().toString()) && interactionDTO.getBorrower().getId() == hasBook.getBorrower().getId());
            List<HasBook> hasBooks = bookStream.collect(Collectors.toList());
            if(!hasBooks.isEmpty()){
                hasBookResource.deleteHasBook(hasBooks.get(0).getId());
                messagingTemplate.convertAndSend("/topic/interaction", interactionDTO);
                return new ResponseEntity<Object>("BOOK_RETURN_SUCCESS", HttpStatus.OK);
            } else {
                return new ResponseEntity<Object>("BOOK_RETURN_FAILED", HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }
        messagingTemplate.convertAndSend("/topic/interaction", interactionDTO);
        return new ResponseEntity<Object>("BOOK_NEW", HttpStatus.OK);
    }

    @JsonView(Views.Borrower.class)
    @RequestMapping(value = "/borrowers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Borrower>> getAllBorrowers(Pageable pageable) throws URISyntaxException {
        return borrowerResource.getAllBorrowers(pageable);
    }


}
