package hexlet.code;

import com.rollbar.notifier.Rollbar;
import hexlet.code.exceptions.DuplicateUsernameException;
import hexlet.code.exceptions.EntityDependOnOthersException;
import hexlet.code.exceptions.EntityNotFoundException;
import hexlet.code.exceptions.IncorrectEntityDataException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@ResponseBody
@AllArgsConstructor
public class ExceptionRestControllerAdvice {

    @Autowired
    private final Rollbar rollbar;

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        rollbar.error(e, e.getMessage());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(DuplicateUsernameException.class)
    public String handleDuplicateUsernameException(DuplicateUsernameException e) {
        rollbar.error(e, e.getMessage());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(EntityDependOnOthersException.class)
    public String handleEntityDependOnOthersException(EntityDependOnOthersException e) {
        rollbar.error(e, e.getMessage());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException e) {
        rollbar.error(e, e.getMessage());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(IncorrectEntityDataException.class)
    public String handleIncorrectEntityDataException(IncorrectEntityDataException e) {
        rollbar.error(e, e.getMessage());
        return e.getMessage();
    }
}
