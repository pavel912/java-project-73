package hexlet.code.services;

import hexlet.code.domain.Label;
import hexlet.code.dto.LabelDto;
import hexlet.code.exceptions.EntityDependOnOthers;
import hexlet.code.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    @Autowired
    TaskRepository taskRepository;

    @Override
    public LabelDto labelToDto(Label label) {
        LabelDto labelDto = new LabelDto();

        labelDto.setId(label.getId());
        labelDto.setName(label.getName());
        labelDto.setCreatedAt(label.getCreatedAt());

        return labelDto;
    }

    @Override
    public void checkLabelAssociatedWithTasks(Label label) {
        taskRepository.findAll().forEach(task -> {
            if (!task.getLabels().stream().filter(l -> l.getId() == label.getId()).toList().isEmpty()) {
                throw new EntityDependOnOthers("Label can be deleted because some tasks are labeled with it");
            }
        });
    }
}
