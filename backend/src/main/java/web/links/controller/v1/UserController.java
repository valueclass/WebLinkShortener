package web.links.controller.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import web.links.dto.PasswordsDto;
import web.links.dto.UserDto;
import web.links.exception.UserNotFoundException;
import web.links.service.UserService;
import web.links.utils.Utils;

@Controller
public class UserController {
    @Autowired
    private UserService users;

    public Mono<ServerResponse> updatePassword(final ServerRequest request) {
        return Utils.userId(request).zipWith(request.bodyToMono(PasswordsDto.class))
                .flatMap(pair -> users.updatePassword(pair.getT1(), pair.getT2()))
                .then(request.session())
                .flatMap(WebSession::invalidate)
                .then(Mono.defer(() -> ServerResponse.ok().build()));
    }

    public Mono<ServerResponse> whoami(final ServerRequest request) {
        return Utils.userId(request)
                .switchIfEmpty(Mono.error(() -> new UserNotFoundException("User was not found")))
                .flatMap(userId -> users.findUserById(userId))
                .flatMap(user -> ServerResponse.ok().bodyValue(user));
    }
}
