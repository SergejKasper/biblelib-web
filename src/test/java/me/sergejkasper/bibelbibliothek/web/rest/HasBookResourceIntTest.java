package me.sergejkasper.bibelbibliothek.web.rest;

import me.sergejkasper.bibelbibliothek.BibelBibliothekApp;
import me.sergejkasper.bibelbibliothek.domain.HasBook;
import me.sergejkasper.bibelbibliothek.repository.HasBookRepository;
import me.sergejkasper.bibelbibliothek.repository.search.HasBookSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the HasBookResource REST controller.
 *
 * @see HasBookResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BibelBibliothekApp.class)
@WebAppConfiguration
@IntegrationTest
public class HasBookResourceIntTest {


    private static final LocalDate DEFAULT_BORROW_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BORROW_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_RETURN_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_RETURN_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_RETURNED = false;
    private static final Boolean UPDATED_RETURNED = true;

    @Inject
    private HasBookRepository hasBookRepository;

    @Inject
    private HasBookSearchRepository hasBookSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restHasBookMockMvc;

    private HasBook hasBook;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        HasBookResource hasBookResource = new HasBookResource();
        ReflectionTestUtils.setField(hasBookResource, "hasBookSearchRepository", hasBookSearchRepository);
        ReflectionTestUtils.setField(hasBookResource, "hasBookRepository", hasBookRepository);
        this.restHasBookMockMvc = MockMvcBuilders.standaloneSetup(hasBookResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        hasBookSearchRepository.deleteAll();
        hasBook = new HasBook();
        hasBook.setBorrowDate(DEFAULT_BORROW_DATE);
        hasBook.setReturnDate(DEFAULT_RETURN_DATE);
        hasBook.setReturned(DEFAULT_RETURNED);
    }

    @Test
    @Transactional
    public void createHasBook() throws Exception {
        int databaseSizeBeforeCreate = hasBookRepository.findAll().size();

        // Create the HasBook

        restHasBookMockMvc.perform(post("/api/has-books")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(hasBook)))
                .andExpect(status().isCreated());

        // Validate the HasBook in the database
        List<HasBook> hasBooks = hasBookRepository.findAll();
        assertThat(hasBooks).hasSize(databaseSizeBeforeCreate + 1);
        HasBook testHasBook = hasBooks.get(hasBooks.size() - 1);
        assertThat(testHasBook.getBorrowDate()).isEqualTo(DEFAULT_BORROW_DATE);
        assertThat(testHasBook.getReturnDate()).isEqualTo(DEFAULT_RETURN_DATE);
        assertThat(testHasBook.isReturned()).isEqualTo(DEFAULT_RETURNED);

        // Validate the HasBook in ElasticSearch
        HasBook hasBookEs = hasBookSearchRepository.findOne(testHasBook.getId());
        assertThat(hasBookEs).isEqualToComparingFieldByField(testHasBook);
    }

    @Test
    @Transactional
    public void checkBorrowDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = hasBookRepository.findAll().size();
        // set the field null
        hasBook.setBorrowDate(null);

        // Create the HasBook, which fails.

        restHasBookMockMvc.perform(post("/api/has-books")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(hasBook)))
                .andExpect(status().isBadRequest());

        List<HasBook> hasBooks = hasBookRepository.findAll();
        assertThat(hasBooks).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkReturnDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = hasBookRepository.findAll().size();
        // set the field null
        hasBook.setReturnDate(null);

        // Create the HasBook, which fails.

        restHasBookMockMvc.perform(post("/api/has-books")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(hasBook)))
                .andExpect(status().isBadRequest());

        List<HasBook> hasBooks = hasBookRepository.findAll();
        assertThat(hasBooks).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkReturnedIsRequired() throws Exception {
        int databaseSizeBeforeTest = hasBookRepository.findAll().size();
        // set the field null
        hasBook.setReturned(null);

        // Create the HasBook, which fails.

        restHasBookMockMvc.perform(post("/api/has-books")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(hasBook)))
                .andExpect(status().isBadRequest());

        List<HasBook> hasBooks = hasBookRepository.findAll();
        assertThat(hasBooks).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllHasBooks() throws Exception {
        // Initialize the database
        hasBookRepository.saveAndFlush(hasBook);

        // Get all the hasBooks
        restHasBookMockMvc.perform(get("/api/has-books?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(hasBook.getId().intValue())))
                .andExpect(jsonPath("$.[*].borrowDate").value(hasItem(DEFAULT_BORROW_DATE.toString())))
                .andExpect(jsonPath("$.[*].returnDate").value(hasItem(DEFAULT_RETURN_DATE.toString())))
                .andExpect(jsonPath("$.[*].returned").value(hasItem(DEFAULT_RETURNED.booleanValue())));
    }

    @Test
    @Transactional
    public void getHasBook() throws Exception {
        // Initialize the database
        hasBookRepository.saveAndFlush(hasBook);

        // Get the hasBook
        restHasBookMockMvc.perform(get("/api/has-books/{id}", hasBook.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(hasBook.getId().intValue()))
            .andExpect(jsonPath("$.borrowDate").value(DEFAULT_BORROW_DATE.toString()))
            .andExpect(jsonPath("$.returnDate").value(DEFAULT_RETURN_DATE.toString()))
            .andExpect(jsonPath("$.returned").value(DEFAULT_RETURNED.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingHasBook() throws Exception {
        // Get the hasBook
        restHasBookMockMvc.perform(get("/api/has-books/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateHasBook() throws Exception {
        // Initialize the database
        hasBookRepository.saveAndFlush(hasBook);
        hasBookSearchRepository.save(hasBook);
        int databaseSizeBeforeUpdate = hasBookRepository.findAll().size();

        // Update the hasBook
        HasBook updatedHasBook = new HasBook();
        updatedHasBook.setId(hasBook.getId());
        updatedHasBook.setBorrowDate(UPDATED_BORROW_DATE);
        updatedHasBook.setReturnDate(UPDATED_RETURN_DATE);
        updatedHasBook.setReturned(UPDATED_RETURNED);

        restHasBookMockMvc.perform(put("/api/has-books")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedHasBook)))
                .andExpect(status().isOk());

        // Validate the HasBook in the database
        List<HasBook> hasBooks = hasBookRepository.findAll();
        assertThat(hasBooks).hasSize(databaseSizeBeforeUpdate);
        HasBook testHasBook = hasBooks.get(hasBooks.size() - 1);
        assertThat(testHasBook.getBorrowDate()).isEqualTo(UPDATED_BORROW_DATE);
        assertThat(testHasBook.getReturnDate()).isEqualTo(UPDATED_RETURN_DATE);
        assertThat(testHasBook.isReturned()).isEqualTo(UPDATED_RETURNED);

        // Validate the HasBook in ElasticSearch
        HasBook hasBookEs = hasBookSearchRepository.findOne(testHasBook.getId());
        assertThat(hasBookEs).isEqualToComparingFieldByField(testHasBook);
    }

    @Test
    @Transactional
    public void deleteHasBook() throws Exception {
        // Initialize the database
        hasBookRepository.saveAndFlush(hasBook);
        hasBookSearchRepository.save(hasBook);
        int databaseSizeBeforeDelete = hasBookRepository.findAll().size();

        // Get the hasBook
        restHasBookMockMvc.perform(delete("/api/has-books/{id}", hasBook.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean hasBookExistsInEs = hasBookSearchRepository.exists(hasBook.getId());
        assertThat(hasBookExistsInEs).isFalse();

        // Validate the database is empty
        List<HasBook> hasBooks = hasBookRepository.findAll();
        assertThat(hasBooks).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchHasBook() throws Exception {
        // Initialize the database
        hasBookRepository.saveAndFlush(hasBook);
        hasBookSearchRepository.save(hasBook);

        // Search the hasBook
        restHasBookMockMvc.perform(get("/api/_search/has-books?query=id:" + hasBook.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hasBook.getId().intValue())))
            .andExpect(jsonPath("$.[*].borrowDate").value(hasItem(DEFAULT_BORROW_DATE.toString())))
            .andExpect(jsonPath("$.[*].returnDate").value(hasItem(DEFAULT_RETURN_DATE.toString())))
            .andExpect(jsonPath("$.[*].returned").value(hasItem(DEFAULT_RETURNED.booleanValue())));
    }
}
