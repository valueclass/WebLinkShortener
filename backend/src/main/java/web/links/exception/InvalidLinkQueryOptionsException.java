package web.links.exception;

public class InvalidLinkQueryOptionsException extends RuntimeException {

    public InvalidLinkQueryOptionsException(final String message) {
        super(message);
    }

    public InvalidLinkQueryOptionsException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
