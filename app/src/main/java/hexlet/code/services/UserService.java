package hexlet.code.services;

import hexlet.code.domain.User;
import hexlet.code.dto.UserDto;

public interface UserService {
    User createUser(UserDto userDto) throws Exception;
    User updateUser(UserDto userDto) throws Exception;
}
