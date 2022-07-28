package web.links.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import web.links.auth.ExtendedUser;
import web.links.repository.UserRepository;

import java.util.Collections;

@Service
public class R2dbcUserService implements UserService {

    @Autowired
    private UserRepository users;

    @Override
    public Mono<UserDetails> findByUsername(final String username) {
        return users.findByUsername(username)
                .map(user -> new ExtendedUser(user.username(), user.password(), Collections.emptyList(), user.userId()));
    }
}