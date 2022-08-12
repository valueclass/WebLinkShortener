package web.links.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import web.links.auth.ExtendedUser;
import web.links.dto.PasswordsDto;
import web.links.dto.UserDto;
import web.links.exception.BadRequestException;
import web.links.exception.UserNotFoundException;
import web.links.model.UserModel;
import web.links.repository.UserRepository;

import java.util.Collections;

@Service
public class R2dbcUserService implements UserService {

    @Autowired
    private UserRepository users;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public Mono<UserDetails> findByUsername(final String username) {
        return users.findByUsername(username)
                .map(user -> new ExtendedUser(user.username(), user.password(), Collections.emptyList(), user.userId()));
    }

    @Override
    public Mono<Void> updatePassword(final String userId, final PasswordsDto passwords) {
        return users.findByUserId(userId)
                .switchIfEmpty(Mono.error(() -> new UserNotFoundException("User not found")))
                .filter(user -> !passwords.getUpdated().equals(passwords.getOld()))
                .switchIfEmpty(Mono.error(() -> new BadRequestException("Same passwords")))
                .filter(user -> encoder.matches(passwords.getOld(), user.password()))
                .switchIfEmpty(Mono.error(() -> new BadCredentialsException("Current passwords mismatch")))
                .flatMap(user -> users.save(user.password(encoder.encode(passwords.getUpdated()))))
                .then();
    }

    @Override
    public Mono<UserDto> findUserById(final String id) {
        return users.findByUserId(id)
                .switchIfEmpty(Mono.error(() -> new UserNotFoundException("User not found")))
                .map(UserDto::fromModel);
    }
}