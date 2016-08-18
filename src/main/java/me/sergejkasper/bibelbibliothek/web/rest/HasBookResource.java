package me.sergejkasper.bibelbibliothek.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import me.sergejkasper.bibelbibliothek.domain.HasBook;
import me.sergejkasper.bibelbibliothek.repository.HasBookRepository;
import me.sergejkasper.bibelbibliothek.repository.search.HasBookSearchRepository;
import me.sergejkasper.bibelbibliothek.web.rest.util.HeaderUtil;
import me.sergejkasper.bibelbibliothek.web.rest.util.PaginationUtil;
import me.sergejkasper.bibelbibliothek.web.views.Views;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.swing.text.View;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing HasBook.
 */
@RestController
@RequestMapping("/api")
public class HasBookResource {

    private final Logger log = LoggerFactory.getLogger(HasBookResource.class);

    @Inject
    private HasBookRepository hasBookRepository;

    @Inject
    private HasBookSearchRepository hasBookSearchRepository;

    /**
     * POST  /has-books : Create a new hasBook.
     *
     * @param hasBook the hasBook to create
     * @return the ResponseEntity with status 201 (Created) and with body the new hasBook, or with status 400 (Bad Request) if the hasBook has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @JsonView(Views.HasBook.class)
    @RequestMapping(value = "/has-books",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<HasBook> createHasBook(@Valid @RequestBody HasBook hasBook) throws URISyntaxException {
        log.debug("REST request to save HasBook : {}", hasBook);
        if (hasBook.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("hasBook", "idexists", "A new hasBook cannot already have an ID")).body(null);
        }
        HasBook result = hasBookRepository.save(hasBook);
        hasBookSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/has-books/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("hasBook", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /has-books : Updates an existing hasBook.
     *
     * @param hasBook the hasBook to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated hasBook,
     * or with status 400 (Bad Request) if the hasBook is not valid,
     * or with status 500 (Internal Server Error) if the hasBook couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @JsonView(Views.HasBook.class)
    @RequestMapping(value = "/has-books",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<HasBook> updateHasBook(@Valid @RequestBody HasBook hasBook) throws URISyntaxException {
        log.debug("REST request to update HasBook : {}", hasBook);
        if (hasBook.getId() == null) {
            return createHasBook(hasBook);
        }
        HasBook result = hasBookRepository.save(hasBook);
        hasBookSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("hasBook", hasBook.getId().toString()))
            .body(result);
    }

    /**
     * GET  /has-books : get all the hasBooks.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of hasBooks in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @JsonView(Views.HasBook.class)
    @RequestMapping(value = "/has-books",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<HasBook>> getAllHasBooks(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of HasBooks");
        Page<HasBook> page = hasBookRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/has-books");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /has-books/:id : get the "id" hasBook.
     *
     * @param id the id of the hasBook to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the hasBook, or with status 404 (Not Found)
     */
    @JsonView(Views.HasBook.class)
    @RequestMapping(value = "/has-books/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<HasBook> getHasBook(@PathVariable Long id) {
        log.debug("REST request to get HasBook : {}", id);
        HasBook hasBook = hasBookRepository.findOne(id);
        return Optional.ofNullable(hasBook)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /has-books/:id : delete the "id" hasBook.
     *
     * @param id the id of the hasBook to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @JsonView(Views.HasBook.class)
    @RequestMapping(value = "/has-books/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteHasBook(@PathVariable Long id) {
        log.debug("REST request to delete HasBook : {}", id);
        hasBookRepository.delete(id);
        hasBookSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("hasBook", id.toString())).build();
    }

    /**
     * SEARCH  /_search/has-books?query=:query : search for the hasBook corresponding
     * to the query.
     *
     * @param query the query of the hasBook search
     * @return the result of the search
     */
    @JsonView(Views.HasBook.class)
    @RequestMapping(value = "/_search/has-books",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<HasBook>> searchHasBooks(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of HasBooks for query {}", query);
        Page<HasBook> page = hasBookSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/has-books");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}

