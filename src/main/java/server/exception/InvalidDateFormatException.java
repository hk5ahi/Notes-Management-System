package server.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidDateFormatException extends RuntimeException {

    private final String responseMessage;

    public InvalidDateFormatException(String message, String responseMessage) {
        super(message);
        this.responseMessage = responseMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
}
