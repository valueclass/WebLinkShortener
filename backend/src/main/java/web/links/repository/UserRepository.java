package web.links.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import web.links.model.UserModel;

@Repository
public interface UserRepository extends R2dbcRepository<UserModel, Integer> {
}
