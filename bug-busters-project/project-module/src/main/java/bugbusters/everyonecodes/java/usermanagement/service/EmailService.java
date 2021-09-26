package bugbusters.everyonecodes.java.usermanagement.service;

import bugbusters.everyonecodes.java.notification.Notification;
import bugbusters.everyonecodes.java.notification.NotificationService;
import bugbusters.everyonecodes.java.usermanagement.data.EmailSchedule;
import bugbusters.everyonecodes.java.usermanagement.data.User;
import bugbusters.everyonecodes.java.usermanagement.data.UserPrivateDTO;
import bugbusters.everyonecodes.java.usermanagement.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDTOMapper userDTOMapper;
    // REVIEW: This seems to be not used.
    private final NotificationService notificationService;

    private final Map<String, String> allowedUsers = new HashMap<>();

    public EmailService(JavaMailSender javaMailSender, UserRepository userRepository, PasswordEncoder passwordEncoder, UserDTOMapper userDTOMapper, NotificationService notificationService) {
        this.javaMailSender = javaMailSender;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDTOMapper = userDTOMapper;
        this.notificationService = notificationService;
    }

    // REVIEW: The name and position of this function seems a bit off. I would have assumed it to be called something like sendPasswordResetMail. Also putting it inside a separate PasswordReset Service, maybe?
    // send email with link that allows password change
    public void sendMail(String email) {
        var oUser = userRepository.findOneByEmail(email);
        if (oUser.isEmpty()) throw new IllegalArgumentException();
        var uuid = UUID.randomUUID().toString();

        // REVIEW: Interesting idea, but be careful, this only works if you do not run your application in a single process.
        //         I would suggest adding a reset-uuid together with a reset-timestamp field to the user.

        // add the user and the uuid to map that allows password change
        allowedUsers.put(oUser.get().getUsername(), uuid);

        var subject = "Reset your Password";
        // REVIEW: This message or at least the URL would perfectly qualify as a setting
        var message = "Please use this dummy link to create a new password:\n https://www.bugbusterseveryonecodes.com/passwordreset/" + uuid;

        var mailMessage = new SimpleMailMessage();

        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        // REVIEW: Also here: Making the email configurable would be better.
        mailMessage.setFrom("bugbusters21@gmail.com");

        javaMailSender.send(mailMessage);
    }

    // REVIEW: The parameter keyword is never used. What was the intention behind it? Is this a bug?
    // send Email with List of Activities
    public void sendListOfActivityMailForKeyword(String to, String keyword, String message, String subject) {
        var mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        // REVIEW: If you are using strings more than once it is better to use a constant.
        mailMessage.setFrom("bugbusters21@gmail.com");
        javaMailSender.send(mailMessage);
    }


    // use the link sent by mail to actually set a new password
    public UserPrivateDTO savePassword(String email, String uuid, String newPassword) throws IllegalArgumentException {
        // REVIEW: I really like your comments. Not too much, not to little.

        // check if user with that email exists
        var oUser = userRepository.findOneByEmail(email);
        // REVIEW: Adding a message to exceptions makes debugging much easier.
        if (oUser.isEmpty()) throw new IllegalArgumentException();
        var userTemp = oUser.get();



        // REVIEW: Most systems also check a timestamp and only allow resets within a certain amount of time (e.g. 4h).
        // check if these values have been added to map by sendMail() method
        if (!allowedUsers.containsKey(userTemp.getUsername()) || !allowedUsers.get(userTemp.getUsername()).equals(uuid)) throw new IllegalArgumentException();

        // validate and save new password
        if (!newPassword.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!?@#$^&+=/_-])(?=\\S+$).{6,100}")) throw new IllegalArgumentException();
        userTemp.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userTemp);

        var userDTO = userDTOMapper.toUserPrivateDTO(userTemp);

        // REVIEW: Just a note, the order here is important - First save, then delete leads to the possibility that you can use the code twice if something crashes between these calls.
        //         If you do it the other way round, it is possible that the process does not work but has already invalidated your link.
        //         This is a well known problem called "the two armies problem" and it is formally unsolvable. You just have to decide with case is more tolerable for you.
        // remove used entry from map
        allowedUsers.remove(userTemp.getUsername());

        return userDTO;
    }

// clear map every day at 3am
    // REVIEW: Very good - You already thought about security and memory. Nevertheless, I would suggest using a
    //         timestamp. This could also lead to concurrency issues if you access your hashmap from 2 threads at
    //         the same time (which you would do if someone resets the pw at 3:00). Also, what would happen if I try
    //         to reset my PW at 2:59, receive a mail at 3:00 and click on the link at 3:01. Would that work?
    @Scheduled(cron= "0 0 3 * * *")
    public void clearMap() {
        allowedUsers.clear();
    }

    // REVIEW: I don't really understand the purpose if this function. Can you elaborate?
// just for testing
    public void addEntryToMap(String key, String value) {
        allowedUsers.put(key, value);
    }


    // REVIEW: Such comments are a good indication that the following code should be in its own service
    //-----------email notifications-------------------

    // REVIEW: What would happen if a user selects DAILY in day 1, changes to WEEKLY on day 6 and to MONTHLY on day 29?
    //every day at 10am
    @Scheduled(cron= "0 0 10 * * ?")
    public void sendEmailNotificationDaily() {
        // REVIEW: In a final application it as a good idea to make all messages configurable to enable i18n.
        var subject = "Daily Notifications from BugBusters";
        var message = "Here are your notifications:\n\n";
        var mailMessage = new SimpleMailMessage();
        List<User> users = userRepository.findByEmailSchedule(EmailSchedule.DAILY);
        // REVIEW: Using a Loop here is a simple approach that is used in real applications, so for small applications or prototypes it is fine most of the time.
        //         Just keep in mind that this does not scale, if you have to balance the load to 2 instances of your application this would send 2 mail per user.
        //         Also, if send fails, no other mails are sent and a restart would lead to duplicate mails.
        //         An easy way to fix this is adding a "sent" flag to the notification, setting it after each sent call.
        //         For the problem with scaling, the easiest way is to add a "send_notifications" config value that is only enabled on the first instance.
        //         Even better would be extracting this part into its own application, so it can be hosted separately.
        //         On big systems this problem is solved using message-queues where a special "database" (message broker) helps with this kind of things.
        for (User user : users) {
            List<Notification> notifications = user.getNotifications();
            if (notifications.isEmpty()) {
                continue;
            }
            // REVIEW: Using streams is nice but can lead to problems if there is a lot of data
            //         You could add a special get to your notification repository (see https://www.baeldung.com/spring-data-jpa-query-by-date).
            //         Especially without a "sent" flag this stream gets bigger with every notification and will make problems if your App runs long enough.
            String result = notifications.stream()
                    .filter(n -> n.getTimestamp().isAfter(LocalDateTime.now().minusHours(24)))
                    .map(this::toEmailString)
                    .collect(Collectors.joining("\n"));
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject(subject);
            mailMessage.setText(message + result);
            // REVIEW: Config or at least a constant
            mailMessage.setFrom("bugbusters21@gmail.com");
            javaMailSender.send(mailMessage);
        }
    }

    //every monday at 10am - weekly
    @Scheduled(cron= "0 0 10 ? * MON")
    public void sendEmailNotificationWeekly() {
        var subject = "Weekly Notifications from BugBusters";
        var message = "Here are your notifications:\n\n";
        var mailMessage = new SimpleMailMessage();
        List<User> users = userRepository.findByEmailSchedule(EmailSchedule.WEEKLY);
        for (User user : users) {
            List<Notification> notifications = user.getNotifications();
            if (notifications.isEmpty()) {
                continue;
            }
            String result = notifications.stream()
                    .filter(n -> n.getTimestamp().isAfter(LocalDateTime.now().minusDays(7)))
                    .map(this::toEmailString)
                    .collect(Collectors.joining("\n"));
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject(subject);
            mailMessage.setText(message + result);
            mailMessage.setFrom("bugbusters21@gmail.com");
            javaMailSender.send(mailMessage);
        }
    }

    //on the first day of every month at 10am - monthly
    @Scheduled(cron= "0 0 10 1 * ?")
    public void sendEmailNotificationMonthly() {
        var subject = "Monthly Notifications from BugBusters";
        var message = "Here are your notifications:\n\n";
        var mailMessage = new SimpleMailMessage();
        List<User> users = userRepository.findByEmailSchedule(EmailSchedule.MONTHLY);
        for (User user : users) {
            List<Notification> notifications = user.getNotifications();
            if (notifications.isEmpty()) {
                continue;
            }
            String result = notifications.stream()
                    .filter(n -> n.getTimestamp().isAfter(LocalDateTime.now().minusMonths(1)))
                    .map(this::toEmailString)
                    .collect(Collectors.joining("\n"));
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject(subject);
            mailMessage.setText(message + result);
            mailMessage.setFrom("bugbusters21@gmail.com");
            javaMailSender.send(mailMessage);
        }
    }

    private String toEmailString(Notification notification) {
        return "From: \"" + notification.getCreator() + "\"\n" +
                "Message: \"" + notification.getMessage() + "\"\n";
    }

    //only for review to show test email for notifications
    public void sendTestHTMLEmail(String usernameInput) {
        Optional<User> oUser = userRepository.findOneByUsername(usernameInput);
        if (oUser.isEmpty()) return;
        String to = oUser.get().getEmail();
        String from = "bugbusters21@gmail.com";
        final String username = from;
        // REVIEW: Never hardcode and commit a password, even if you reset it after the tests. This will be seen as highly unprofessional.
        //         Also, there are bots on github looking for such things all the time. If you accidentally push a valid PW to a public
        //         GitHub expect to get 0wN3d fast.
        final String password = "kgatvfvoxznoyxsx";
        // REVIEW: You might want to use something like https://mailtrap.io/ or if local https://github.com/mailhog/MailHog
        String host = "smtp.gmail.com";
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            // REVIEW: There are some libraries that can help you template emails. Just you know. Creating mails directly
            //         inside code is not something you would do in production-ready applications. In prototypes, it might be fine.
            MimeMessage message = new MimeMessage(session);
            MimeMessageHelper helper = new MimeMessageHelper(message,  true, "utf-8");
            helper.setTo(to);
            String body = "<h3><font color=black>Your Email notification is working!</font></h3><br>";
            body += "<font color=black><p><i>Here are your notifications:</i><br><br>";
            List<Notification> notifications = oUser.get().getNotifications();
            String notificationsAsString = notifications.stream()
                    .map(this::toEmailStringHTML)
                    .collect(Collectors.joining("<br>"));
            body += notificationsAsString;
            body += "<br><br>" + "Click on Link to unsubscribe: "
                    + "http://localhost:8080/users/notifications/email/unsubscribe/" + usernameInput + "</p></font>";
            helper.setText(body, true);
            helper.setSubject("Test Notifications from BugBusters");
            helper.setFrom(from);
            File file = new File("bug-busters-project/project-module/src/main/resources/BugBustersSmall.png");
            helper.addAttachment("BugBustersSmall.png", file);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String toEmailStringHTML(Notification notification) {
        return "<b>From:</b> \"" + notification.getCreator() + "\"<br>" +
                "<b>Message:</b> \"" + notification.getMessage() + "\"<br>";
    }


    // REVIEW: Returning errors es strings is not a good idea. Just throw an exception and catch it at the last layer if you have to return strings to the user.
    public String registerEmailNotification(String username, EmailSchedule schedule) {
        Optional<User> oUser = userRepository.findOneByUsername(username);
        if (oUser.isEmpty()) return "Oops, something went wrong.";
        User user = oUser.get();
        if (!user.getEmailSchedule().equals(EmailSchedule.NONE)) {
            return username + " already has a "
                    + schedule.toString().toLowerCase() + " notification subscription.";
        }
        user.setEmailSchedule(schedule);
        userRepository.save(user);
        return username + " is registered for " + schedule.toString().toLowerCase() + " email notification";
    }

    public String unsubscribeEmailNotification(String username) {
        Optional<User> oUser = userRepository.findOneByUsername(username);
        if (oUser.isEmpty()) return "Oops, something went wrong.";
        User user = oUser.get();
        user.setEmailSchedule(EmailSchedule.NONE);
        userRepository.save(user);
        return "You have successfully unsubscribed from your email notifications!";
    }
}