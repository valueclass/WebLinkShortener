package web.links.utils;

import org.springframework.security.core.Authentication;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import web.links.auth.ExtendedUserDetails;

import java.util.Random;

public class Utils {
    private static final char[] CHARS = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890_-".toCharArray();
    private static final Random RANDOM = new Random();

    public static String generateId(final int length) {
        final char[] chars = new char[length];

        chars[0] = CHARS[RANDOM.nextInt(CHARS.length - 2)];

        for (int i = 1; i < length; i++) {
            char c = CHARS[RANDOM.nextInt(CHARS.length)];
            chars[i] = c;
        }

        return new String(chars);
    }

    public static String generateAlphanumericId(final int length) {
        final char[] chars = new char[length];

        for (int i = 0; i < length; i++) {
            char c = CHARS[RANDOM.nextInt(CHARS.length - 2)];
            chars[i] = c;
        }

        return new String(chars);
    }

    public static Mono<String> userId(final ServerRequest request) {
        return request.principal().flatMap(p -> {
            if (p instanceof Authentication auth && auth.getPrincipal() instanceof ExtendedUserDetails details) {
                return Mono.just(details.getUserId());
            }

            return Mono.empty();
        });
    }
}
