package web.links.service;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import web.links.dto.PasswordsDto;
import web.links.dto.UserDto;

public interface UserService extends ReactiveUserDetailsService {
    Mono<Void> updatePassword(String username, PasswordsDto passwords);
}
