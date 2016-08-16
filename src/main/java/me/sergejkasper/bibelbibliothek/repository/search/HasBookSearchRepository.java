package me.sergejkasper.bibelbibliothek.repository.search;

import me.sergejkasper.bibelbibliothek.domain.HasBook;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the HasBook entity.
 */
public interface HasBookSearchRepository extends ElasticsearchRepository<HasBook, Long> {
}
