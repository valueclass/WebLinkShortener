package web.links.exception;

public class InvalidLinkException extends BadRequestException {

    public InvalidLinkException(final String message) {
        super(message);
    }

    public InvalidLinkException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
