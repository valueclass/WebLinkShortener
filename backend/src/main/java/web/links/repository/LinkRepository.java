package web.links.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import web.links.model.LinkModel;

@Repository
public interface LinkRepository extends R2dbcRepository<LinkModel, Integer> {
}
