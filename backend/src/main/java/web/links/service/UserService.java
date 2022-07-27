package web.links.service;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Flux;
import web.links.dto.UserDto;

public interface UserService extends ReactiveUserDetailsService {

}
