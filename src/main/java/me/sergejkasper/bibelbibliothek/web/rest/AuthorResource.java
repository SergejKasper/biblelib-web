package me.sergejkasper.bibelbibliothek.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import me.sergejkasper.bibelbibliothek.domain.Author;
import me.sergejkasper.bibelbibliothek.repository.AuthorRepository;
import me.sergejkasper.bibelbibliothek.repository.search.AuthorSearchRepository;
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
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Author.
 */
@RestController
@RequestMapping("/api")
public class AuthorResource {

    private final Logger log = LoggerFactory.getLogger(AuthorResource.class);

    @Inject
    private AuthorRepository authorRepository;

    @Inject
    private AuthorSearchRepository authorSearchRepository;

    /**
     * POST  /authors : Create a new author.
     *
     * @param author the author to create
     * @return the ResponseEntity with status 201 (Created) and with body the new author, or with status 400 (Bad Request) if the author has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/authors",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Author> createAuthor(@Valid @RequestBody Author author) throws URISyntaxException {
        log.debug("REST request to save Author : {}", author);
        if (author.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("author", "idexists", "A new author cannot already have an ID")).body(null);
        }
        Author result = authorRepository.save(author);
        authorSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/authors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("author", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /authors : Updates an existing author.
     *
     * @param author the author to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated author,
     * or with status 400 (Bad Request) if the author is not valid,
     * or with status 500 (Internal Server Error) if the author couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/authors",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Author> updateAuthor(@Valid @RequestBody Author author) throws URISyntaxException {
        log.debug("REST request to update Author : {}", author);
        if (author.getId() == null) {
            return createAuthor(author);
        }
        Author result = authorRepository.save(author);
        authorSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("author", author.getId().toString()))
            .body(result);
    }

    /**
     * GET  /authors : get all the authors.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of authors in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/authors",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Author>> getAllAuthors(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Authors");
        Page<Author> page = authorRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/authors");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /authors/:id : get the "id" author.
     *
     * @param id the id of the author to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the author, or with status 404 (Not Found)
     */
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/authors/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Author> getAuthor(@PathVariable Long id) {
        log.debug("REST request to get Author : {}", id);
        Author author = authorRepository.findOne(id);
        return Optional.ofNullable(author)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /authors/:id : delete the "id" author.
     *
     * @param id the id of the author to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/authors/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        log.debug("REST request to delete Author : {}", id);
        authorRepository.delete(id);
        authorSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("author", id.toString())).build();
    }

    /**
     * SEARCH  /_search/authors?query=:query : search for the author corresponding
     * to the query.
     *
     * @param query the query of the author search
     * @return the result of the search
     */
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/_search/authors",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Author>> searchAuthors(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Authors for query {}", query);
        Page<Author> page = authorSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/authors");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
