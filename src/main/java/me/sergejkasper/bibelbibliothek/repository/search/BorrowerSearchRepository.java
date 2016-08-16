package me.sergejkasper.bibelbibliothek.repository.search;

import me.sergejkasper.bibelbibliothek.domain.Borrower;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Borrower entity.
 */
public interface BorrowerSearchRepository extends ElasticsearchRepository<Borrower, Long> {
}
