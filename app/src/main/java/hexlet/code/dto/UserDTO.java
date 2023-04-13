package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private long id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private Instant createdAt;
}
