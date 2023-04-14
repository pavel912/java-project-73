package hexlet.code.services;

import hexlet.code.domain.User;
import hexlet.code.dto.UserDTO;
import hexlet.code.exceptions.UserNotFoundException;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    private static final Exception USER_DOES_NOT_EXIST = new Exception("User with this ID does not exist");

    private static final Exception INCORRECT_DATA_EXCEPTION = new Exception("Incorrect user data");
    private static final Exception USER_ALREADY_EXISTS = new Exception("User with this email already exists");

    @Override
    public User createUser(UserDTO userDto) throws Exception {
        if (isIncorrectUserData(userDto)) {
            throw INCORRECT_DATA_EXCEPTION;
        }

        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw USER_ALREADY_EXISTS;
        }

        User user = userDtoToUser(userDto);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(UserDTO userDto) throws Exception {
        if (isIncorrectUserData(userDto)) {
            throw INCORRECT_DATA_EXCEPTION;
        }

        User userWithSameLogin = userRepository.findByEmail(userDto.getEmail());

        if (userWithSameLogin != null && userWithSameLogin.getId() != userDto.getId()) {
            throw USER_ALREADY_EXISTS;
        }

        User user = userRepository.findById(userDto.getId());

        if (user == null) {
            throw USER_DOES_NOT_EXIST;
        }

        User updatedUser = userDtoToUser(userDto);
        updatedUser.setId(user.getId());
        updatedUser.setCreatedAt(user.getCreatedAt());

        return userRepository.save(updatedUser);
    }

    private User userDtoToUser(UserDTO userDto) {
        User user = new User();

        if (userDto == null) {
            return user;
        }

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        return user;

    }

    private boolean isEmptyOrDoesNotHaveRequiredLength(String value, int length) {
        return value == null || value.length() < length;
    }

    private boolean isIncorrectUserData(UserDTO userDto) {
        if (userDto == null) {
            return true;
        }
        if (isEmptyOrDoesNotHaveRequiredLength(userDto.getFirstName(), 1)) {
            return true;
        }
        if (isEmptyOrDoesNotHaveRequiredLength(userDto.getLastName(), 1)) {
            return true;
        }
        if (!EmailValidator.getInstance().isValid(userDto.getEmail())) {
            return true;
        }
        if (isEmptyOrDoesNotHaveRequiredLength(userDto.getPassword(), 3)) {
            return true;
        }

        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("user"));

        User user = userRepository.findByEmail(username);

        if (user == null) {
            throw new UserNotFoundException("User with such username does not exist");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
