package hexlet.code.services;

import hexlet.code.domain.Task;
import hexlet.code.dto.TaskDtoInput;
import hexlet.code.dto.TaskDtoOutput;

public interface TaskService {
    Task createTask(TaskDtoInput taskDtoInput);
    Task updateTask(TaskDtoInput taskDtoInput);
    TaskDtoOutput taskToTaskDto(Task task);
}
