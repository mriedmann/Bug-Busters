package bugbusters.everyonecodes.java.usermanagement.endpoints;

import bugbusters.everyonecodes.java.notification.Notification;
import bugbusters.everyonecodes.java.notification.NotificationService;
import bugbusters.everyonecodes.java.usermanagement.data.EmailSchedule;
import bugbusters.everyonecodes.java.usermanagement.data.User;
import bugbusters.everyonecodes.java.usermanagement.data.UserPrivateDTO;
import bugbusters.everyonecodes.java.usermanagement.rolemanagement.volunteer.VolunteerService;
import bugbusters.everyonecodes.java.usermanagement.service.EmailService;
import bugbusters.everyonecodes.java.usermanagement.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

// REVIEW: I noticed that you are mixing DTO and non-DTO objects in your return types. Normally DTOs are used to build
//         a stable API, separating it from the implementation. This makes it easier to change internal stuff without
//         breaking other things. What was the idea behind the DTOs?
@RestController
@RequestMapping("/users")
public class UserEndpoint {

    private final UserService userService;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final VolunteerService volunteerService;

    public UserEndpoint(UserService userService, EmailService emailService, NotificationService notificationService, VolunteerService volunteerService) {
        this.userService = userService;
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.volunteerService = volunteerService;
    }

    @PostMapping("/register")
    User registerUser(@Valid @RequestBody User user){
        try {
            return userService.saveUser(user);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid Password", e
            );
        }
    }

    @GetMapping("/passwordreset/{email}")
    void forgotPassword(@PathVariable String email) {
        emailService.sendMail(email);
    }

    @PostMapping("/passwordreset/{email}/{uuid}")
    UserPrivateDTO setPassword(@PathVariable String email, @PathVariable String uuid, @RequestBody String password) {
        return emailService.savePassword(email, uuid, password);
    }

    @Secured({"ROLE_VOLUNTEER", "ROLE_INDIVIDUAL", "ROLE_ORGANIZATION"})
    @GetMapping("/notifications")
    List<Notification> getNotifications(Authentication authentication){
        return notificationService.findAllNotificationsChronologicalByUsername(authentication.getName());
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/notifications/email/test/daily")
    void sendDailyEmailTest() {
        volunteerService.sendDailyEmail();
    }

    // REVIEW: Using strings as a return type makes your API harder to use. I would suggest wrapping it in a simple
    //         return type, so it gets auto-serialized to JSON. Alternatively, just returning HTTP 200 without a message
    //         is also ok, as long as you use correct error-codes.
    @Secured({"ROLE_VOLUNTEER", "ROLE_INDIVIDUAL", "ROLE_ORGANIZATION"})
    @PutMapping("/notifications/email/{schedule}")
    String registerEmailNotifications(Authentication authentication, @PathVariable String schedule) {
        return emailService.registerEmailNotification(authentication.getName(), EmailSchedule.valueOf(schedule.toUpperCase()));
    }

    //endpoint only for review
    @Secured({"ROLE_ADMIN"})
    @GetMapping("/notifications/email/test/{username}")
    void sendTestHtmlEmail(@PathVariable String username) {
        emailService.sendTestHTMLEmail(username);
    }


    @GetMapping("/notifications/email/unsubscribe/{username}")
    String unsubscribeEmailNotification(@PathVariable String username) {
        return emailService.unsubscribeEmailNotification(username);

    }
}