package me.sergejkasper.bibelbibliothek.web.rest;

import com.codahale.metrics.annotation.Timed;
import me.sergejkasper.bibelbibliothek.domain.Borrower;
import me.sergejkasper.bibelbibliothek.repository.BorrowerRepository;
import me.sergejkasper.bibelbibliothek.repository.search.BorrowerSearchRepository;
import me.sergejkasper.bibelbibliothek.web.rest.util.HeaderUtil;
import me.sergejkasper.bibelbibliothek.web.rest.util.PaginationUtil;
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
 * REST controller for managing Borrower.
 */
@RestController
@RequestMapping("/api")
public class BorrowerResource {

    private final Logger log = LoggerFactory.getLogger(BorrowerResource.class);

    @Inject
    private BorrowerRepository borrowerRepository;

    @Inject
    private BorrowerSearchRepository borrowerSearchRepository;

    /**
     * POST  /borrowers : Create a new borrower.
     *
     * @param borrower the borrower to create
     * @return the ResponseEntity with status 201 (Created) and with body the new borrower, or with status 400 (Bad Request) if the borrower has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/borrowers",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Borrower> createBorrower(@Valid @RequestBody Borrower borrower) throws URISyntaxException {
        log.debug("REST request to save Borrower : {}", borrower);
        if (borrower.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("borrower", "idexists", "A new borrower cannot already have an ID")).body(null);
        }
        Borrower result = borrowerRepository.save(borrower);
        borrowerSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/borrowers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("borrower", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /borrowers : Updates an existing borrower.
     *
     * @param borrower the borrower to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated borrower,
     * or with status 400 (Bad Request) if the borrower is not valid,
     * or with status 500 (Internal Server Error) if the borrower couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/borrowers",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Borrower> updateBorrower(@Valid @RequestBody Borrower borrower) throws URISyntaxException {
        log.debug("REST request to update Borrower : {}", borrower);
        if (borrower.getId() == null) {
            return createBorrower(borrower);
        }
        Borrower result = borrowerRepository.save(borrower);
        borrowerSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("borrower", borrower.getId().toString()))
            .body(result);
    }

    /**
     * GET  /borrowers : get all the borrowers.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of borrowers in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/borrowers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Borrower>> getAllBorrowers(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Borrowers");
        Page<Borrower> page = borrowerRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/borrowers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /borrowers/:id : get the "id" borrower.
     *
     * @param id the id of the borrower to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the borrower, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/borrowers/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Borrower> getBorrower(@PathVariable Long id) {
        log.debug("REST request to get Borrower : {}", id);
        Borrower borrower = borrowerRepository.findOne(id);
        return Optional.ofNullable(borrower)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /borrowers/:id : delete the "id" borrower.
     *
     * @param id the id of the borrower to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/borrowers/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteBorrower(@PathVariable Long id) {
        log.debug("REST request to delete Borrower : {}", id);
        borrowerRepository.delete(id);
        borrowerSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("borrower", id.toString())).build();
    }

    /**
     * SEARCH  /_search/borrowers?query=:query : search for the borrower corresponding
     * to the query.
     *
     * @param query the query of the borrower search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/borrowers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Borrower>> searchBorrowers(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Borrowers for query {}", query);
        Page<Borrower> page = borrowerSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/borrowers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
