package web.links.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import web.links.model.UserModel;

@Repository
public interface UserRepository extends R2dbcRepository<UserModel, Integer> {
    @Query("SELECT * FROM users WHERE username = :username")
    Mono<UserModel> findByUsername(String username);

    @Query("SELECT * FROM users WHERE userId = :userId")
    Mono<UserModel> findByUserId(String userId);
}
