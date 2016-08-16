package me.sergejkasper.bibelbibliothek.web.rest;

import me.sergejkasper.bibelbibliothek.BibelBibliothekApp;
import me.sergejkasper.bibelbibliothek.domain.Borrower;
import me.sergejkasper.bibelbibliothek.repository.BorrowerRepository;
import me.sergejkasper.bibelbibliothek.repository.search.BorrowerSearchRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the BorrowerResource REST controller.
 *
 * @see BorrowerResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BibelBibliothekApp.class)
@WebAppConfiguration
@IntegrationTest
public class BorrowerResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_EMAIL = "AAAAA";
    private static final String UPDATED_EMAIL = "BBBBB";
    private static final String DEFAULT_PHONE_NUMBER = "AAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBB";

    @Inject
    private BorrowerRepository borrowerRepository;

    @Inject
    private BorrowerSearchRepository borrowerSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restBorrowerMockMvc;

    private Borrower borrower;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BorrowerResource borrowerResource = new BorrowerResource();
        ReflectionTestUtils.setField(borrowerResource, "borrowerSearchRepository", borrowerSearchRepository);
        ReflectionTestUtils.setField(borrowerResource, "borrowerRepository", borrowerRepository);
        this.restBorrowerMockMvc = MockMvcBuilders.standaloneSetup(borrowerResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        borrowerSearchRepository.deleteAll();
        borrower = new Borrower();
        borrower.setName(DEFAULT_NAME);
        borrower.setEmail(DEFAULT_EMAIL);
        borrower.setPhoneNumber(DEFAULT_PHONE_NUMBER);
    }

    @Test
    @Transactional
    public void createBorrower() throws Exception {
        int databaseSizeBeforeCreate = borrowerRepository.findAll().size();

        // Create the Borrower

        restBorrowerMockMvc.perform(post("/api/borrowers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(borrower)))
                .andExpect(status().isCreated());

        // Validate the Borrower in the database
        List<Borrower> borrowers = borrowerRepository.findAll();
        assertThat(borrowers).hasSize(databaseSizeBeforeCreate + 1);
        Borrower testBorrower = borrowers.get(borrowers.size() - 1);
        assertThat(testBorrower.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBorrower.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testBorrower.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);

        // Validate the Borrower in ElasticSearch
        Borrower borrowerEs = borrowerSearchRepository.findOne(testBorrower.getId());
        assertThat(borrowerEs).isEqualToComparingFieldByField(testBorrower);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = borrowerRepository.findAll().size();
        // set the field null
        borrower.setName(null);

        // Create the Borrower, which fails.

        restBorrowerMockMvc.perform(post("/api/borrowers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(borrower)))
                .andExpect(status().isBadRequest());

        List<Borrower> borrowers = borrowerRepository.findAll();
        assertThat(borrowers).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBorrowers() throws Exception {
        // Initialize the database
        borrowerRepository.saveAndFlush(borrower);

        // Get all the borrowers
        restBorrowerMockMvc.perform(get("/api/borrowers?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(borrower.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
                .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER.toString())));
    }

    @Test
    @Transactional
    public void getBorrower() throws Exception {
        // Initialize the database
        borrowerRepository.saveAndFlush(borrower);

        // Get the borrower
        restBorrowerMockMvc.perform(get("/api/borrowers/{id}", borrower.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(borrower.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBorrower() throws Exception {
        // Get the borrower
        restBorrowerMockMvc.perform(get("/api/borrowers/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBorrower() throws Exception {
        // Initialize the database
        borrowerRepository.saveAndFlush(borrower);
        borrowerSearchRepository.save(borrower);
        int databaseSizeBeforeUpdate = borrowerRepository.findAll().size();

        // Update the borrower
        Borrower updatedBorrower = new Borrower();
        updatedBorrower.setId(borrower.getId());
        updatedBorrower.setName(UPDATED_NAME);
        updatedBorrower.setEmail(UPDATED_EMAIL);
        updatedBorrower.setPhoneNumber(UPDATED_PHONE_NUMBER);

        restBorrowerMockMvc.perform(put("/api/borrowers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedBorrower)))
                .andExpect(status().isOk());

        // Validate the Borrower in the database
        List<Borrower> borrowers = borrowerRepository.findAll();
        assertThat(borrowers).hasSize(databaseSizeBeforeUpdate);
        Borrower testBorrower = borrowers.get(borrowers.size() - 1);
        assertThat(testBorrower.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBorrower.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testBorrower.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);

        // Validate the Borrower in ElasticSearch
        Borrower borrowerEs = borrowerSearchRepository.findOne(testBorrower.getId());
        assertThat(borrowerEs).isEqualToComparingFieldByField(testBorrower);
    }

    @Test
    @Transactional
    public void deleteBorrower() throws Exception {
        // Initialize the database
        borrowerRepository.saveAndFlush(borrower);
        borrowerSearchRepository.save(borrower);
        int databaseSizeBeforeDelete = borrowerRepository.findAll().size();

        // Get the borrower
        restBorrowerMockMvc.perform(delete("/api/borrowers/{id}", borrower.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean borrowerExistsInEs = borrowerSearchRepository.exists(borrower.getId());
        assertThat(borrowerExistsInEs).isFalse();

        // Validate the database is empty
        List<Borrower> borrowers = borrowerRepository.findAll();
        assertThat(borrowers).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBorrower() throws Exception {
        // Initialize the database
        borrowerRepository.saveAndFlush(borrower);
        borrowerSearchRepository.save(borrower);

        // Search the borrower
        restBorrowerMockMvc.perform(get("/api/_search/borrowers?query=id:" + borrower.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(borrower.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER.toString())));
    }
}
