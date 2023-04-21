package hexlet.code.controllers;

import com.querydsl.core.types.Predicate;
import hexlet.code.domain.Task;
import hexlet.code.domain.User;
import hexlet.code.dto.TaskRequestDto;
import hexlet.code.dto.TaskResponseDto;
import hexlet.code.exceptions.EntityNotFoundException;
import hexlet.code.repository.TaskRepository;
import hexlet.code.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import liquibase.repackaged.org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("${base-url}" + "/tasks")
public class TaskController {
    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TaskService taskService;

    private static final String ONLY_OWNER_BY_ID = """
            @taskRepository.findById(#id).getAuthor().getEmail() == authentication.getName()
            """;

    @Operation(summary = "Get task by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Information retrieved",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Task not found")})
    @GetMapping(path = "/{id}")
    public TaskResponseDto getTask(@PathVariable long id) {
        Task task = taskRepository.findById(id);

        if (task == null) {
            throw new EntityNotFoundException("Task with id " + id + " does not exist");
        }

        return taskService.taskToTaskDto(task);
    }

    @Operation(summary = "Get all tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Information retrieved",
                    content = @Content(schema = @Schema(implementation = User.class)))})
    @GetMapping(path = "")
    public List<TaskResponseDto> getTasks(@QuerydslPredicate(root = Task.class) Predicate predicate) {
        List<Task> tasks = IterableUtils.toList(taskRepository.findAll(predicate));

        return tasks.stream().map(x -> taskService.taskToTaskDto(x)).toList();
    }

    @Operation(summary = "Create task")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task created",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "422", description = "Incorrect task data")})
    @PostMapping(path = "")
    public TaskResponseDto createTask(@RequestBody @Valid TaskRequestDto taskRequestDto) {
        Task task = taskService.createTask(taskRequestDto);

        return taskService.taskToTaskDto(task);
    }

    @Operation(summary = "Update task")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "422", description = "Incorrect task data")})
    @PutMapping(path = "/{id}")
    public TaskResponseDto updateTask(@PathVariable long id, @RequestBody @Valid TaskRequestDto taskRequestDto) {
        taskRequestDto.setId(id);
        Task task = taskService.updateTask(taskRequestDto);

        return taskService.taskToTaskDto(task);
    }

    @Operation(summary = "Delete task")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task deleted",
                    content = @Content(schema = @Schema(implementation = User.class)))})
    @DeleteMapping(path = "/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteTask(@PathVariable long id) {
        Task task = taskRepository.findById(id);

        if (task == null) {
            throw new EntityNotFoundException("Task with id " + id + " does not exist");
        }

        taskRepository.delete(task);
    }
}
