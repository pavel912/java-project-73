package hexlet.code.controllers;

import hexlet.code.domain.User;
import hexlet.code.dto.UserDto;
import hexlet.code.exceptions.EntityNotFoundException;
import hexlet.code.repository.UserRepository;
import hexlet.code.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import liquibase.repackaged.org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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


@RestController
@RequestMapping("${base-url}" + "/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#id).getEmail() == authentication.getName()
            """;

    @Operation(summary = "Get user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Information retrieved",
            content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found")})
    @GetMapping(path = "/{id}")
    public UserDto getUser(@PathVariable long id) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        return userService.userToUserDto(user);
    }

    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "Information retrieved",
            content = @Content(schema = @Schema(implementation = User.class)))
    @GetMapping(path = "")
    public List<UserDto> getUsers() {
        List<User> users = IterableUtils.toList(userRepository.findAll());

        return users.stream().map(x -> userService.userToUserDto(x)).toList();
    }

    @Operation(summary = "Create new user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User created",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "422", description = "Incorrect user data")})
    @PostMapping(path = "")
    public UserDto createUser(@RequestBody @Valid final UserDto userDto) throws Exception {
            User user = userService.createUser(userDto);
            return userService.userToUserDto(user);
    }

    @Operation(summary = "Update user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "422", description = "Incorrect user data")})
    @PutMapping(path = "/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public UserDto updateUser(@PathVariable long id, @RequestBody @Valid final UserDto userDto) throws Exception {
            userDto.setId(id);
            User user = userService.updateUser(userDto);
            return userService.userToUserDto(user);
    }

    @Operation(summary = "Delete user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "422", description = "User is connected to at least one task")})
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteUser(@PathVariable long id) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        userService.checkUserAssociatedWithTasks(user);

        userRepository.delete(user);
    }
}
