package upc.req_quality.exception;

public class NotFoundException extends ComponentException {

    public NotFoundException(String message) {
        super(message,404,"Not found");
    }
}
