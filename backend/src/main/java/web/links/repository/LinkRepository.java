package web.links.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import web.links.model.LinkModel;

@Repository
public interface LinkRepository extends R2dbcRepository<LinkModel, Integer> {
    @Query("SELECT * FROM links WHERE link_id = :linkId")
    Mono<LinkModel> findByLinkId(String linkId);

    @Query("SELECT id, name FROM links WHERE name = :name")
    Mono<LinkModel> findBySource(String source);
}
