package hexlet.code.controllers;

import hexlet.code.domain.Label;
import hexlet.code.domain.User;
import hexlet.code.dto.LabelDto;
import hexlet.code.exceptions.EntityNotFoundException;
import hexlet.code.repository.LabelRepository;
import hexlet.code.services.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import liquibase.repackaged.org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("${base-url}" + "/labels")
public class LabelController {
    @Autowired
    LabelRepository labelRepository;

    @Autowired
    LabelService labelService;

    @Operation(summary = "Get task label by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Information retrieved",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Label not found")})
    @GetMapping(path = "/{id}")
    public LabelDto getLabel(@PathVariable long id) {
        Label label = labelRepository.findById(id);

        if (label == null) {
            throw new EntityNotFoundException("Label with id " + id + " does not exist");
        }

        return labelService.labelToDto(label);
    }

    @Operation(summary = "Get all task labels")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Information retrieved",
                    content = @Content(schema = @Schema(implementation = User.class)))})
    @GetMapping(path = "")
    public List<LabelDto> getLabels() {
        List<Label> labels = IterableUtils.toList(labelRepository.findAll());

        return labels.stream().map(label -> labelService.labelToDto(label)).toList();
    }

    @Operation(summary = "Create label")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Label created",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "422", description = "Incorrect label data")})
    @PostMapping(path = "")
    @ResponseStatus(CREATED)
    public LabelDto createLabel(@RequestBody @Valid LabelDto labelDto) {
        Label label = new Label();
        label.setName(labelDto.getName());

        Label resultingLabel = labelRepository.save(label);

        return labelService.labelToDto(resultingLabel);
    }

    @Operation(summary = "Update label")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Label updated",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Label not found"),
            @ApiResponse(responseCode = "422", description = "Incorrect label data")})
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

    @Operation(summary = "Delete label")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Label deleted",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Label not found")})
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
