package hexlet.code.controllers;

import hexlet.code.domain.TaskStatus;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.exceptions.EntityNotFoundException;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.services.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(path = "${base-url}" + "/statuses")
public class TaskStatusController {
    @Autowired
    TaskStatusRepository taskStatusRepository;

    @Autowired
    TaskStatusService taskStatusService;

    @Operation(summary = "Get all task statuses")
    @ApiResponse(responseCode = "200", description = "Information retrieved",
            content = @Content(schema = @Schema(implementation = TaskStatus.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "")
    public List<TaskStatusDto> getAllTaskStatuses() {
        List<TaskStatus> taskStatuses = IterableUtils.toList(taskStatusRepository.findAll());

        return taskStatuses.stream().map(taskStatus -> taskStatusService.taskStatusToDto(taskStatus)).toList();
    }

    @Operation(summary = "Get task status by id")
    @ApiResponse(responseCode = "200", description = "Information retrieved",
            content = @Content(schema = @Schema(implementation = TaskStatus.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{id}")
    public TaskStatusDto getTaskStatus(@PathVariable long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id);

        if (taskStatus == null) {
            throw new EntityNotFoundException("Task status with id" + id + "not found");
        }

        return taskStatusService.taskStatusToDto(taskStatus);
    }

    @Operation(summary = "Create task status")
    @ApiResponse(responseCode = "201", description = "Task status created")
    @PostMapping(path = "")
    @ResponseStatus(CREATED)
    public TaskStatusDto createTaskStatus(@RequestBody @Valid final TaskStatusDto taskStatusDto) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(taskStatusDto.getName());

        TaskStatus resultingTaskStatus = taskStatusRepository.save(taskStatus);

        return taskStatusService.taskStatusToDto(resultingTaskStatus);
    }

    @Operation(summary = "Update task status")
    @ApiResponse(responseCode = "200", description = "Task status updated")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{id}")
    public TaskStatusDto updateTaskStatus(
            @PathVariable long id,
            @RequestBody @Valid final TaskStatusDto taskStatusDto) {
        TaskStatus taskStatus = taskStatusRepository.findById(id);

        if (taskStatus == null) {
            throw new EntityNotFoundException("Task status with id" + id + "not found");
        }

        taskStatus.setName(taskStatusDto.getName());
        TaskStatus resultingTaskStatus = taskStatusRepository.save(taskStatus);

        return taskStatusService.taskStatusToDto(resultingTaskStatus);
    }

    @Operation(summary = "Delete task status")
    @ApiResponse(responseCode = "200", description = "Task status deleted")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/{id}")
    public void deleteTaskStatus(@PathVariable long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id);

        if (taskStatus == null) {
            throw new EntityNotFoundException("Task status with id" + id + "not found");
        }

        taskStatusService.checkStatusAssociatedWithTasks(taskStatus);

        taskStatusRepository.delete(taskStatus);
    }
}
