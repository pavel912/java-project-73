package hexlet.code.services;

import hexlet.code.domain.User;
import hexlet.code.dto.UserDto;
import hexlet.code.exceptions.DuplicateUsernameException;
import hexlet.code.exceptions.EntityNotFoundException;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
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

    @Override
    public User createUser(UserDto userDto) throws EntityNotFoundException, DuplicateUsernameException {
        if (userDto == null) {
            throw new EntityNotFoundException("Empty user data");
        }

        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new DuplicateUsernameException("User with email" + userDto.getEmail() + "already exists");
        }

        User user = userDtoToUser(userDto);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(UserDto userDto) throws EntityNotFoundException, DuplicateUsernameException {
        if (userDto == null) {
            throw new EntityNotFoundException("Empty user data");
        }

        User userWithSameLogin = userRepository.findByEmail(userDto.getEmail());

        if (userWithSameLogin != null && userWithSameLogin.getId() != userDto.getId()) {
            throw new DuplicateUsernameException("User with email" + userDto.getEmail() + "already exists");
        }

        User user = userRepository.findById(userDto.getId());

        if (user == null) {
            throw new EntityNotFoundException("User with id" + userDto.getId() + "does not exist");
        }

        User updatedUser = userDtoToUser(userDto);
        updatedUser.setId(user.getId());
        updatedUser.setCreatedAt(user.getCreatedAt());

        return userRepository.save(updatedUser);
    }

    private User userDtoToUser(UserDto userDto) {
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("user"));

        User user = userRepository.findByEmail(username);

        if (user == null) {
            throw new EntityNotFoundException("User with such username does not exist");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
