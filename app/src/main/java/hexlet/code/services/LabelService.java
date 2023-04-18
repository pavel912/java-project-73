package hexlet.code.services;

import hexlet.code.domain.Label;
import hexlet.code.dto.LabelDto;

public interface LabelService {
    LabelDto labelToDto(Label label);

    void checkLabelAssociatedWithTasks(Label label);
}
