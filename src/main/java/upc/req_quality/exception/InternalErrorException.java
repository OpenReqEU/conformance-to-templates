package upc.req_quality.exception;

public class InternalErrorException extends ComponentException {

    public InternalErrorException (String message) {
        super(message,500,"Internal Error: " + message);
    }
}
