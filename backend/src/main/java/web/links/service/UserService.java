package web.links.service;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;
import web.links.dto.PasswordsDto;
import web.links.dto.UserDto;
import web.links.model.UserModel;

public interface UserService extends ReactiveUserDetailsService {
    Mono<Void> updatePassword(String username, PasswordsDto passwords);
    Mono<UserDto> findUserById(String id);
}
