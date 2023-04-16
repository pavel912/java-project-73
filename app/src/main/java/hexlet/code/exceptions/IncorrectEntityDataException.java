package hexlet.code.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class IncorrectEntityDataException extends RuntimeException {
    public IncorrectEntityDataException(String message) {
        super(message);
    }
}
