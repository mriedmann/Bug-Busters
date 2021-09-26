package bugbusters.everyonecodes.java.notification;

import bugbusters.everyonecodes.java.usermanagement.data.User;
import bugbusters.everyonecodes.java.usermanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    // REVIEW: You can use a sort-by with JPA repositories (see https://stackoverflow.com/questions/19733464/order-by-date-asc-with-spring-data)
    public List<Notification> findAllNotificationsChronologicalByUsername(String username){
        Optional<User> oUser = userRepository.findOneByUsername(username);
        if (oUser.isEmpty()) {
            return new ArrayList<>();
        }
        List<Notification> result = oUser.get().getNotifications();
        return result.stream().sorted(Comparator.comparing(Notification::getTimestamp).reversed()).collect(Collectors.toList());
    }

    public void saveNotification(Notification notification, String username){
        Optional<User> oResult = userRepository.findOneByUsername(username);
        if (oResult.isEmpty() || notification == null){
            return;
        }
        User result = oResult.get();
        // REVIEW: You are relaying on a cascaded save. I would add the user to the notification and save it to the notification repository.
        result.getNotifications().add(notification);
        userRepository.save(result);
    }
}
