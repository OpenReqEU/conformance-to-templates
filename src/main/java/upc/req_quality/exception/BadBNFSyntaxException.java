package upc.req_quality.exception;

public class BadBNFSyntaxException extends ComponentException {
    public BadBNFSyntaxException(String message) {
        super(message,400,"Bad BNF syntax: " + message);
    }
}
