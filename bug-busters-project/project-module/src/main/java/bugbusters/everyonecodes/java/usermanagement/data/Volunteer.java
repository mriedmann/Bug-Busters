package bugbusters.everyonecodes.java.usermanagement.data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
public class Volunteer extends User {

    @ElementCollection
    private Set<String> skills = new HashSet<>();

    @ElementCollection
    private Map<String, EmailSchedule> registeredKeywords = new HashMap<>();

    public Volunteer() {
    }

    public Volunteer(User user) {
        this(user.getUsername(), user.getPassword(),  user.getRole(), user.getFullName(), user.getBirthday(), user.getAddress(), user.getEmail(), user.getDescription());
    }

    public Volunteer(String username, String password, String role, String fullName, LocalDate birthday, String address, String email, String description) {
        super(username, password, role, fullName, birthday, address, email, description);
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }

    public Map<String, EmailSchedule> getRegisteredKeywords() {
        return registeredKeywords;
    }

    public void setRegisteredKeywords(Map<String, EmailSchedule> registeredKeywords) {
        this.registeredKeywords = registeredKeywords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
