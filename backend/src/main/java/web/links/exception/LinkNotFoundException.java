package web.links.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LinkNotFoundException extends RuntimeException {

    public LinkNotFoundException(final String message) {
        super(message);
    }

    public LinkNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
