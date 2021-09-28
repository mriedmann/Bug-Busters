package bugbusters.everyonecodes.java.usermanagement.data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Individual extends User {

    public Individual() {
    }

    public Individual(User user) {
        this(user.getUsername(), user.getPassword(),  user.getRole(), user.getFullName(), user.getBirthday(), user.getAddress(), user.getEmail(), user.getDescription());
    }

    public Individual(String username, String password, String role, String fullName, LocalDate birthday, String address, String email, String description) {
        super(username, password, role, fullName, birthday, address, email, description);
    }

}
