package hexlet.code.controllers;

import com.querydsl.core.types.Predicate;
import hexlet.code.domain.Task;
import hexlet.code.dto.TaskDtoInput;
import hexlet.code.dto.TaskDtoOutput;
import hexlet.code.exceptions.EntityNotFoundException;
import hexlet.code.repository.TaskRepository;
import hexlet.code.services.TaskService;
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

    @GetMapping(path = "/{id}")
    public TaskDtoOutput getTask(@PathVariable long id) {
        Task task = taskRepository.findById(id);

        if (task == null) {
            throw new EntityNotFoundException("Task with id " + id + " does not exist");
        }

        return taskService.taskToTaskDto(task);
    }

    @GetMapping(path = "")
    public List<TaskDtoOutput> getTasks(@QuerydslPredicate(root = Task.class) Predicate predicate) {
        List<Task> tasks = IterableUtils.toList(taskRepository.findAll(predicate));

        return tasks.stream().map(x -> taskService.taskToTaskDto(x)).toList();
    }

    @PostMapping(path = "")
    public TaskDtoOutput createTask(@RequestBody @Valid TaskDtoInput taskDtoInput) {
        Task task = taskService.createTask(taskDtoInput);

        return taskService.taskToTaskDto(task);
    }

    @PutMapping(path = "/{id}")
    public TaskDtoOutput updateTask(@PathVariable long id, @RequestBody @Valid TaskDtoInput taskDtoInput) {
        taskDtoInput.setId(id);

        Task task = taskService.updateTask(taskDtoInput);


        return taskService.taskToTaskDto(task);
    }

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
