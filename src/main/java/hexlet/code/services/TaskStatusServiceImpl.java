package hexlet.code.services;

import hexlet.code.domain.TaskStatus;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.exceptions.EntityDependOnOthers;
import hexlet.code.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    @Autowired
    TaskRepository taskRepository;
    @Override
    public TaskStatusDto taskStatusToDto(TaskStatus taskStatus) {
        TaskStatusDto taskStatusDto = new TaskStatusDto();
        taskStatusDto.setId(taskStatus.getId());
        taskStatusDto.setName(taskStatus.getName());
        taskStatusDto.setCreatedAt(taskStatus.getCreatedAt());
        return taskStatusDto;
    }

    @Override
    public void checkStatusAssociatedWithTasks(TaskStatus taskStatus) {
        taskRepository.findAll().forEach(task -> {
            if (task.getTaskStatus() == taskStatus) {
                throw new EntityDependOnOthers("This task status is associated with at least one task");
            }
        });
    }
}
