package hexlet.code.services;

import hexlet.code.domain.User;
import hexlet.code.dto.UserDto;

public interface UserService {
    User createUser(UserDto userDTO) throws Exception;
    User updateUser(UserDto userDTO) throws Exception;
}
