package bugbusters.everyonecodes.java.notification;

import org.springframework.data.jpa.repository.JpaRepository;

// REVIEW: ok
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
