package hexlet.code.services;

import hexlet.code.domain.Label;
import hexlet.code.domain.Task;
import hexlet.code.domain.TaskStatus;
import hexlet.code.domain.User;
import hexlet.code.dto.TaskRequestDto;
import hexlet.code.dto.TaskResponseDto;
import hexlet.code.exceptions.EntityNotFoundException;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {
    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TaskStatusRepository taskStatusRepository;

    @Autowired
    UserService userService;

    @Autowired
    TaskStatusService taskStatusService;

    @Autowired
    LabelRepository labelRepository;

    @Autowired
    LabelService labelService;

    @Override
    public Task createTask(TaskRequestDto taskRequestDto) {
        TaskStatus taskStatus = taskStatusRepository.findById(taskRequestDto.getTaskStatusId());
        User author = userService.getCurrentUser();
        User executor = userRepository.findById(taskRequestDto.getExecutorId());
        List<Label> labels = (List<Label>) labelRepository.findAllById(taskRequestDto.getLabelIds());

        Task task = new Task();
        task.setName(taskRequestDto.getName());
        task.setDescription(taskRequestDto.getDescription());
        task.setTaskStatus(taskStatus);
        task.setAuthor(author);
        task.setExecutor(executor);
        task.setLabels(labels);

        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(TaskRequestDto taskRequestDto) {
        Task task = taskRepository.findById(taskRequestDto.getId());
        if (task == null) {
            throw new EntityNotFoundException("Task with id " + taskRequestDto.getId() + " does not exist");
        }

        TaskStatus taskStatus = taskStatusRepository.findById(taskRequestDto.getTaskStatusId());
        User executor = userRepository.findById(taskRequestDto.getExecutorId());
        List<Label> labels = (List<Label>) labelRepository.findAllById(taskRequestDto.getLabelIds());

        task.setName(taskRequestDto.getName());
        task.setDescription(taskRequestDto.getDescription());
        task.setTaskStatus(taskStatus);
        task.setExecutor(executor);
        task.setLabels(labels);

        return taskRepository.save(task);
    }

    @Override
    public TaskResponseDto taskToTaskDto(Task task) {
        TaskResponseDto taskResponseDto = new TaskResponseDto();
        taskResponseDto.setId(task.getId());
        taskResponseDto.setName(task.getName());
        taskResponseDto.setDescription(task.getDescription());
        taskResponseDto.setTaskStatus(taskStatusService.taskStatusToDto(task.getTaskStatus()));
        taskResponseDto.setAuthor(userService.userToUserDto(task.getAuthor()));
        taskResponseDto.setExecutor(userService.userToUserDto(task.getExecutor()));
        taskResponseDto.setCreatedAt(task.getCreatedAt());
        taskResponseDto.setLabels(
                task
                        .getLabels()
                        .stream()
                        .map(label -> labelService.labelToDto(label))
                        .toList());

        return taskResponseDto;
    }
}
