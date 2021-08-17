package bugbusters.everyonecodes.java.activities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Optional<Activity> getActivityByTitle(String title);

    List<Activity> findByVolunteer_username(String username);

    List<Activity> findByClient_username(String username);


}
