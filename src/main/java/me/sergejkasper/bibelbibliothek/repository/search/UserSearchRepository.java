package me.sergejkasper.bibelbibliothek.repository.search;

import me.sergejkasper.bibelbibliothek.domain.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the User entity.
 */
public interface UserSearchRepository extends ElasticsearchRepository<User, Long> {
}
