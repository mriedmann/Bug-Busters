package bugbusters.everyonecodes.java.usermanagement.repository;

import bugbusters.everyonecodes.java.usermanagement.data.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    Optional<Volunteer> findOneByUsername(String username);
}
