package hexlet.code.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class IncorrectTokenException extends RuntimeException {
    public IncorrectTokenException(String message) {
        super(message);
    }
}
