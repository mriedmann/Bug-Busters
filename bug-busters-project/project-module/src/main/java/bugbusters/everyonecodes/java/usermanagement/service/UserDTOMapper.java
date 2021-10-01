package bugbusters.everyonecodes.java.usermanagement.service;

import bugbusters.everyonecodes.java.usermanagement.api.UserDTO;
import bugbusters.everyonecodes.java.usermanagement.api.UserPrivateDTO;
import bugbusters.everyonecodes.java.usermanagement.api.UserPublicDTO;
import bugbusters.everyonecodes.java.usermanagement.data.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class UserDTOMapper {

    private final LocalDateNowProvider provider;

    public UserDTOMapper(LocalDateNowProvider provider) {
        this.provider = provider;
    }

    public UserPrivateDTO toUserPrivateDTO(User user) {
        return new UserPrivateDTO(
                user.getUsername(),
                user.getRole(),
                user.getFullName(),
                user.getBirthday(),
                user.getAddress(),
                user.getEmail(),
                user.getDescription()
        );
    }

    public UserPublicDTO toUserPublicDTO(User user) {
        if (user == null) {
            return null;
        }
        LocalDate birthday = user.getBirthday();
        Integer age = null;
        if (birthday != null) {
            age = calculateAge(birthday, provider.getDateNow());
        }
        List<Integer> ratings = user.getRatings();
        Double rating = RatingCalcUtil.calculateRating(ratings);
        return new UserPublicDTO(user.getUsername(), user.getFullName(), age, user.getDescription(), rating, user.getExperience());
    }

    public User fromUserDTO(UserDTO userDTO, User user) {
        user.setUsername(userDTO.getUsername());
        user.setRole(userDTO.getRole());
        user.setFullName(userDTO.getFullName());
        user.setBirthday(userDTO.getBirthday());
        user.setAddress(userDTO.getAddress());
        user.setEmail(userDTO.getEmail());
        user.setDescription(userDTO.getDescription());
        return user;
    }

    public UserDTO toUserDTO(User user, UserDTO userDTO) {
        userDTO.setUsername(user.getUsername());
        userDTO.setRole(user.getRole());
        userDTO.setFullName(user.getFullName());
        userDTO.setBirthday(user.getBirthday());
        userDTO.setAddress(user.getAddress());
        userDTO.setEmail(user.getEmail());
        userDTO.setDescription(user.getDescription());
        return userDTO;
    }

    Integer calculateAge(LocalDate birthDate, LocalDate currentDate) {
        return Period.between(birthDate, currentDate).getYears();
    }
}
