package hexlet.code.controllers;

import hexlet.code.domain.TaskStatus;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.exceptions.EntityNotFoundException;
import hexlet.code.repository.TaskStatusRepository;
import liquibase.repackaged.org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(path = "${base-url}" + "/statuses")
public class TaskStatusController {
    @Autowired
    TaskStatusRepository taskStatusRepository;

    @GetMapping(path = "")
    public List<TaskStatusDto> getAllTaskStatuses() {
        List<TaskStatus> taskStatuses = IterableUtils.toList(taskStatusRepository.findAll());

        return taskStatuses.stream().map(taskStatus -> {
            TaskStatusDto taskStatusDto = new TaskStatusDto();
            taskStatusDto.setId(taskStatus.getId());
            taskStatusDto.setName(taskStatus.getName());
            taskStatusDto.setCreatedAt(taskStatus.getCreatedAt());
            return taskStatusDto;
        }).toList();
    }

    @GetMapping(path = "/{id}")
    public TaskStatusDto getTaskStatus(@PathVariable long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id);

        if (taskStatus == null) {
            throw new EntityNotFoundException("Task status with id" + id + "not found");
        }

        TaskStatusDto taskStatusDto = new TaskStatusDto();
        taskStatusDto.setId(taskStatus.getId());
        taskStatusDto.setName(taskStatus.getName());
        taskStatusDto.setCreatedAt(taskStatus.getCreatedAt());

        return taskStatusDto;
    }

    @PostMapping(path = "")
    public TaskStatusDto createTaskStatus(@RequestBody @Valid final TaskStatusDto taskStatusDto) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(taskStatusDto.getName());

        TaskStatus resultingTaskStatus = taskStatusRepository.save(taskStatus);

        TaskStatusDto resultingTaskStatusDto = new TaskStatusDto();
        resultingTaskStatusDto.setId(resultingTaskStatus.getId());
        resultingTaskStatusDto.setName(resultingTaskStatus.getName());
        resultingTaskStatusDto.setCreatedAt(resultingTaskStatus.getCreatedAt());

        return resultingTaskStatusDto;
    }

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

        TaskStatusDto resultingTaskStatusDto = new TaskStatusDto();
        resultingTaskStatusDto.setId(resultingTaskStatus.getId());
        resultingTaskStatusDto.setName(resultingTaskStatus.getName());
        resultingTaskStatusDto.setCreatedAt(resultingTaskStatus.getCreatedAt());

        return resultingTaskStatusDto;
    }

    @DeleteMapping(path = "/{id}")
    public void deleteTaskStatus(@PathVariable long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id);

        if (taskStatus == null) {
            throw new EntityNotFoundException("Task status with id" + id + "not found");
        }

        taskStatusRepository.delete(taskStatus);
    }
}
