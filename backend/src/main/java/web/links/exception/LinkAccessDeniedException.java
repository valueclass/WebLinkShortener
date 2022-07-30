package web.links.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class LinkAccessDeniedException extends RuntimeException {
    
    public LinkAccessDeniedException(final String message) {
        super(message);
    }

    public LinkAccessDeniedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
