package hexlet.code.controllers;
import hexlet.code.domain.User;
import hexlet.code.dto.UserDto;
import hexlet.code.exceptions.EntityNotFoundException;
import hexlet.code.repository.UserRepository;
import hexlet.code.services.UserService;
import liquibase.repackaged.org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("${base-url}" + "/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#id).getEmail() == authentication.getName()
            """;

    @GetMapping(path = "/{id}")
    public UserDto getUser(@PathVariable long id) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new EntityNotFoundException("User with id" + id + "not found");
        }

        return userToUserDto(user);
    }

    @GetMapping(path = "")
    public List<UserDto> getUsers() {
        List<User> users = IterableUtils.toList(userRepository.findAll());

        return users.stream().map(this::userToUserDto).toList();
    }

    @PostMapping(path = "")
    public UserDto createUser(@RequestBody @Valid final UserDto userDto) throws Exception {
            User user = userService.createUser(userDto);
            return userToUserDto(user);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public UserDto updateUser(@PathVariable long id, @RequestBody @Valid final UserDto userDto) throws Exception {
            userDto.setId(id);
            User user = userService.updateUser(userDto);
            return userToUserDto(user);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteUser(@PathVariable long id) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new EntityNotFoundException("User with id" + id + "not found");
        }

        userRepository.delete(user);
    }

    private UserDto userToUserDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setCreatedAt(user.getCreatedAt());

        return userDto;

    }
}
