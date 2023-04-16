package hexlet.code.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Data
@Getter
@Setter
public class TaskStatusDto {
    private long id;
    @NotBlank(message = "Task Status Name can't be empty")
    private String name;
    private Instant createdAt;
}
