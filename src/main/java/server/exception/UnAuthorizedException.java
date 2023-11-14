package server.exception;

public class UnAuthorizedException extends RuntimeException {
    private final String responseMessage;

    public UnAuthorizedException(String message, String responseMessage) {
        super(message);
        this.responseMessage = responseMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
}
