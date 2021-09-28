package bugbusters.everyonecodes.java.usermanagement;

import bugbusters.everyonecodes.java.usermanagement.data.*;
import bugbusters.everyonecodes.java.usermanagement.service.UserDTOMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserFactory {

    private final UserDTOMapper userDTOMapper;
    private final PasswordEncoder passwordEncoder;

    public UserFactory(UserDTOMapper userDTOMapper, PasswordEncoder passwordEncoder) {
        this.userDTOMapper = userDTOMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserDTO userDTO) {
        User user = switch (userDTO.getRole()) {
            case "ROLE_VOLUNTEER" -> new Volunteer();
            case "ROLE_ORGANIZATION" -> new Organization();
            case "ROLE_INDIVIDUAL" -> new Individual();
            default -> throw new IllegalArgumentException();
        };

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encodedPassword);

        return this.userDTOMapper.fromUserDTO(userDTO, user);
    }
}
