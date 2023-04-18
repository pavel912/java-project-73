package hexlet.code.controllers;

import hexlet.code.domain.Label;
import hexlet.code.dto.LabelDto;
import hexlet.code.exceptions.EntityNotFoundException;
import hexlet.code.repository.LabelRepository;
import hexlet.code.services.LabelService;
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
@RequestMapping("${base-url}" + "/labels")
public class LabelController {
    @Autowired
    LabelRepository labelRepository;

    @Autowired
    LabelService labelService;

    @GetMapping(path = "/{id}")
    public LabelDto getLabel(@PathVariable long id) {
        Label label = labelRepository.findById(id);

        if (label == null) {
            throw new EntityNotFoundException("Label with id " + id + " does not exist");
        }

        return labelService.labelToDto(label);
    }

    @GetMapping(path = "")
    public List<LabelDto> getLabels() {
        List<Label> labels = IterableUtils.toList(labelRepository.findAll());

        return labels.stream().map(label -> labelService.labelToDto(label)).toList();
    }

    @PostMapping(path = "")
    public LabelDto createLabel(@RequestBody @Valid LabelDto labelDto) {
        Label label = new Label();
        label.setName(labelDto.getName());

        Label resultingLabel = labelRepository.save(label);

        return labelService.labelToDto(resultingLabel);
    }

    @PutMapping(path = "/{id}")
    public LabelDto updateLabel(@PathVariable long id, @RequestBody @Valid LabelDto labelDto) {
        Label label = labelRepository.findById(id);

        if (label == null) {
            throw new EntityNotFoundException("Label with id " + id + " does not exist");
        }

        label.setName(labelDto.getName());

        Label resultingLabel = labelRepository.save(label);

        return labelService.labelToDto(resultingLabel);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteLabel(@PathVariable long id) {
        Label label = labelRepository.findById(id);

        if (label == null) {
            throw new EntityNotFoundException("Label with id " + id + " does not exist");
        }

        labelService.checkLabelAssociatedWithTasks(label);

        labelRepository.delete(label);
    }
}
