package hexlet.code.services;

import hexlet.code.domain.TaskStatus;
import hexlet.code.dto.TaskStatusDto;

public interface TaskStatusService {
    TaskStatusDto taskStatusToDto(TaskStatus taskStatus);

    void checkStatusAssociatedWithTasks(TaskStatus taskStatus);
}
