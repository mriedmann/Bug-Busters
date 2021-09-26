package bugbusters.everyonecodes.java.usermanagement.rolemanagement.volunteer;

import bugbusters.everyonecodes.java.usermanagement.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    // REVIEW: Try to avoid unused methods, especially in repositories.
    boolean existsByUser_username(String username);
    Optional<Volunteer> findOneByUser_username(String username);
}
