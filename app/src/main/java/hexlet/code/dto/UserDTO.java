package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;

@Data
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private long id;

    @NotBlank(message = "FirstName can't be empty")
    private String firstName;

    @NotBlank(message = "LastName can't be empty")
    private String lastName;

    @NotBlank
    @Email(message = "Email is in incorrect format")
    private String email;

    @Size(min = 3, message = "Password should be not shorter than 3 characters")
    private String password;

    private Instant createdAt;
}
