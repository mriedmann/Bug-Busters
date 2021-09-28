package bugbusters.everyonecodes.java.usermanagement.service;

import bugbusters.everyonecodes.java.usermanagement.data.User;
import bugbusters.everyonecodes.java.usermanagement.data.UserDTO;
import bugbusters.everyonecodes.java.usermanagement.data.UserPrivateDTO;
import bugbusters.everyonecodes.java.usermanagement.data.UserPublicDTO;
import bugbusters.everyonecodes.java.usermanagement.repository.UserRepository;
import bugbusters.everyonecodes.java.usermanagement.UserFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserDTOMapper mapper;
    private final UserFactory userFactory;

    public UserService(UserRepository userRepository, UserDTOMapper mapper, UserFactory userFactory) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.userFactory = userFactory;
    }

    //regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$^&+=])(?=\\S+$).{6,}")
    //password must contain at least 1 digit, 1 lower and upper case character, 1 special character, no whitespaces and must be at least 6 characters long
    public UserDTO saveUser(UserDTO userDTO) throws IllegalArgumentException {
        if (!userDTO.getPassword().matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!?@#$^&+=/_-])(?=\\S+$).{6,100}")) throw new IllegalArgumentException();
        User user = userFactory.createUser(userDTO);
        userRepository.save(user);
        return mapper.toUserDTO(user, userDTO);
    }

    public Optional<UserPrivateDTO> editUserData(UserPrivateDTO input, String username) {
        var oUser = getUserByUsername(username);
        if (oUser.isEmpty()) return Optional.empty();
        var user = oUser.get();
        user.setFullName(input.getFullName());
        user.setBirthday(input.getBirthday());
        user.setAddress(input.getAddress());
        user.setEmail(input.getEmail());
        user.setDescription(input.getDescription());
        return Optional.of(mapper.toUserPrivateDTO(userRepository.save(user)));
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findOneByUsername(username);
    }

    public Optional<UserPrivateDTO> viewUserPrivateData(String username) {
        return getUserByUsername(username).map(user -> mapper.toUserPrivateDTO(user));
    }

    public Optional<UserPublicDTO> viewUserPublicData(String username) {
        return userRepository.findOneByUsername(username).map(user -> mapper.toUserPublicDTO(user));
    }
}

