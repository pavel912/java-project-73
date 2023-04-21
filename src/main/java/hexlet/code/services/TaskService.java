package hexlet.code.services;

import hexlet.code.domain.Task;
import hexlet.code.dto.TaskRequestDto;
import hexlet.code.dto.TaskResponseDto;

public interface TaskService {
    Task createTask(TaskRequestDto taskRequestDto);
    Task updateTask(TaskRequestDto taskRequestDto);
    TaskResponseDto taskToTaskDto(Task task);
}
