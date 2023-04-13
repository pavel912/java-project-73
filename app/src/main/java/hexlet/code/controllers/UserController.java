package hexlet.code.controllers;
import hexlet.code.domain.User;
import hexlet.code.dto.UserDTO;
import hexlet.code.repository.UserRepository;
import hexlet.code.services.UserService;
import liquibase.repackaged.org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("${base-url}" + "/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    ResponseEntity<UserDTO> badRequestResponse = new ResponseEntity<UserDTO>(HttpStatus.UNPROCESSABLE_ENTITY);

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getUser(@PathVariable long id) {
        User user = userRepository.findById(id);

        if (user == null) {
            return formResponse("User with this ID does not exist", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return formResponse(userToUserDto(user), HttpStatus.OK);
    }

    @GetMapping(path = "")
    public List<UserDTO> getUsers() {
        List<User> users = IterableUtils.toList(userRepository.findAll());

        return users.stream().map(this::userToUserDto).toList();
    }

    @PostMapping(path = "")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDto) {
        try {
            User user = userService.createUser(userDto);
            return formResponse(userToUserDto(user), HttpStatus.OK);
        } catch (Exception e) {
            return formResponse(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @RequestBody UserDTO userDto) {
        try {
            userDto.setId(id);
            User user = userService.updateUser(userDto);
            return formResponse(userToUserDto(user), HttpStatus.OK);
        } catch (Exception e) {
            return formResponse(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        User user = userRepository.findById(id);

        if (user == null) {
            return formResponse("User with this ID does not exist", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        userRepository.delete(user);

        return formResponse(userToUserDto(user), HttpStatus.OK);
    }

    private UserDTO userToUserDto(User user) {
        UserDTO userDto = new UserDTO();

        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setCreatedAt(user.getCreatedAt());

        return userDto;

    }

    private ResponseEntity<?> formResponse(Object returnObject, HttpStatus status) {
        return new ResponseEntity<>(returnObject, status);
    }
}
