package bugbusters.everyonecodes.java.usermanagement.service;

import bugbusters.everyonecodes.java.usermanagement.data.User;
import bugbusters.everyonecodes.java.usermanagement.data.UserPrivateDTO;
import bugbusters.everyonecodes.java.usermanagement.data.UserPublicDTO;
import bugbusters.everyonecodes.java.usermanagement.repository.UserRepository;
import bugbusters.everyonecodes.java.usermanagement.rolemanagement.RoleFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDTOMapper mapper;
    private final RoleFactory roleFactory;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserDTOMapper mapper, RoleFactory roleFactory) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
        this.roleFactory = roleFactory;
    }

    // REVIEW: Try to avoid comments that are code. They tend to rot quickly and will confuse people
    //regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$^&+=])(?=\\S+$).{6,}")
    //password must contain at least 1 digit, 1 lower and upper case character, 1 special character, no whitespaces and must be at least 6 characters long
    public User saveUser(User user) throws IllegalArgumentException {
        // REVIEW: Limiting the PW to 100 characters is a bit arbitrary but could also avoid bufferoverflow or simple DOS attacks, so I like the idea. Question is, why limiting the password but not any other input? ;)
        if (!user.getPassword().matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!?@#$^&+=/_-])(?=\\S+$).{6,100}")) throw new IllegalArgumentException();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user = userRepository.save(user);
        roleFactory.createRole(user);
        return user;
    }

    // REVIEW: Why not using the username from the input?
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

    // REVIEW: I personally don't like this "layering". There is some literature that states that you should not use
    // repositories directly inside your controllers and I assume that this is the reason you chose to wrap this
    // call. For me it does not add any benefit, simply adding more complexity => I would simply remove it.
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findOneByUsername(username);
    }

    public Optional<UserPrivateDTO> viewUserPrivateData(String username) {
        // REVIEW: Just a hint - You should be able to call it like this ".map(mapper::toUserPrivateDTO)"
        return getUserByUsername(username).map(user -> mapper.toUserPrivateDTO(user));
    }

    public Optional<UserPublicDTO> viewUserPublicData(String username) {
        return userRepository.findOneByUsername(username).map(user -> mapper.toUserPublicDTO(user));
    }
}

