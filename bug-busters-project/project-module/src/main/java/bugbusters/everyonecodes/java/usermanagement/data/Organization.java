package bugbusters.everyonecodes.java.usermanagement.data;

import bugbusters.everyonecodes.java.usermanagement.rolemanagement.Client;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Organization extends User implements Client  {

    public Organization() {
    }

    public Organization(User user) {
        this(user.getUsername(), user.getPassword(),  user.getRole(), user.getFullName(), user.getBirthday(), user.getAddress(), user.getEmail(), user.getDescription());
    }

    public Organization(String username, String password, String role, String fullName, LocalDate birthday, String address, String email, String description) {
        super(username, password, role, fullName, birthday, address, email, description);
    }

    @Override
    public User getUser() {
        return this;
    }
}
