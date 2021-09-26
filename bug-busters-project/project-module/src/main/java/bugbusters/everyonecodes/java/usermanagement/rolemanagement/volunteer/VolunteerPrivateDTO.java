package bugbusters.everyonecodes.java.usermanagement.rolemanagement.volunteer;

import bugbusters.everyonecodes.java.usermanagement.data.UserPrivateDTO;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.Objects;

// REVIEW: Could need some polymorphism ;)
public class VolunteerPrivateDTO {

    @Valid
    private UserPrivateDTO user;
    // REVIEW: Why no list?
    @Pattern(regexp = "^[a-zA-Z\s;]*$")
    private String skills;

    public VolunteerPrivateDTO() {
    }

    public VolunteerPrivateDTO(UserPrivateDTO user, String skills) {
        this.user = user;
        this.skills = skills;
    }

    // REVIEW: Normally you try to keep DTOs as "flat" as possible. It is no real problem but maybe just extending
    //         the UserPrivateDTO could also work, reducing a bit of the nesting.
    public UserPrivateDTO getUser() {
        return user;
    }

    public void setUser(UserPrivateDTO user) {
        this.user = user;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VolunteerPrivateDTO that = (VolunteerPrivateDTO) o;
        return Objects.equals(user, that.user) && Objects.equals(skills, that.skills);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, skills);
    }

    @Override
    public String toString() {
        return "VolunteerPrivateDTO{" +
                "user=" + user +
                ", skills='" + skills + '\'' +
                '}';
    }
}
