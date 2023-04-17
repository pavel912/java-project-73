package hexlet.code.services;

import hexlet.code.domain.User;
import hexlet.code.dto.UserDto;

public interface UserService {
    User createUser(UserDto userDto) throws Exception;
    User updateUser(UserDto userDto) throws Exception;
    UserDto userToUserDto(User user);
    User userDtoToUser(UserDto userDto);

    void checkUserAssociatedWithTasks(User user);

    String getCurrentUserName();

    User getCurrentUser();
}
