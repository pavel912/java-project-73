package hexlet.code.services;

import hexlet.code.domain.User;
import hexlet.code.dto.UserDTO;

public interface UserService {
    User createUser(UserDTO userDTO) throws Exception;
    User updateUser(UserDTO userDTO) throws Exception;
}
