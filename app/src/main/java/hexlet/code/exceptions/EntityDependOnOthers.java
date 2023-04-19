package hexlet.code.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class EntityDependOnOthers extends RuntimeException {
    public EntityDependOnOthers(String message) {
        super(message);
    }
}
