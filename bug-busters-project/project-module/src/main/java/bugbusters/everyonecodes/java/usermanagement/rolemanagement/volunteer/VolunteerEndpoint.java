package bugbusters.everyonecodes.java.usermanagement.rolemanagement.volunteer;

import bugbusters.everyonecodes.java.activities.ActivityDTO;
import bugbusters.everyonecodes.java.activities.ActivityService;
import bugbusters.everyonecodes.java.search.FilterActivity;
import bugbusters.everyonecodes.java.usermanagement.data.EmailSchedule;
import bugbusters.everyonecodes.java.usermanagement.rolemanagement.organization.ClientPublicDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/volunteer")
@Secured("ROLE_VOLUNTEER")
public class VolunteerEndpoint {

    private final VolunteerService volunteerService;
    private final ActivityService activityService;

    public VolunteerEndpoint(VolunteerService volunteerService, ActivityService activityService) {
        this.volunteerService = volunteerService;
        this.activityService = activityService;
    }

    // REVIEW: The name "login" is a bit confusing here. I would suggest profile or private or something like that.
    @GetMapping("/login")
    VolunteerPrivateDTO viewVolunteerPrivateData(Authentication authentication) {
        return volunteerService.viewVolunteerPrivateData(authentication.getName()).orElse(null);
    }

    @PutMapping("/edit")
    VolunteerPrivateDTO editVolunteerData(@Valid @RequestBody VolunteerPrivateDTO edits, Authentication authentication) {
        return volunteerService.editVolunteerData(edits, authentication.getName()).orElse(null);
    }

    @GetMapping("/view")
    VolunteerPublicDTO viewVolunteerPublicData(Authentication authentication) {
        return volunteerService.viewVolunteerPublicData(authentication.getName()).orElse(null);
    }

    // REVIEW: Maybe this was a requirement but if you have a "whoami" endpoint, returning the currently logged-in user
    //         you can assume that a client can access its own data using the same interfaces as other users data.
    //         This reduces your number of endpoints and might get rid of the need to process the current user.
    //         This also makes caching way easier which is a complex topic in big systems.
    @GetMapping("/view/{username}")
    ClientPublicDTO viewClientPublicData(@PathVariable String username) {
        return volunteerService.viewClientPublicData(username).orElse(null);
    }

    // REVIEW: I personally think that simple DTO mapping calls can live in controllers (endpoints) if it has a good
    //         reason. In your case it would reduce your number of functions significantly. Also, because you should
    //         not use DTOs anywhere else in your code you will not have the problem that you would call your endpoints
    //         internally (that would be a bad idea).
    //         BUT: We used this "wrapping" in some of our products. In bigger projects we are using special mapper-services
    //         now, if I recall correctly.
    @GetMapping("/activities/list/pending")
    List<ActivityDTO> listAllPendingActivities() {
        return volunteerService.listAllPendingActivities();
    }

    // REVIEW: It is quite uncommon to present search parameters inside the url. It is a very interesting idea but
    //         can make troubles with special characters and stuff. If you really allow "free-text" I would have used
    //         a POST endpoint.
    //         Also, because search is such a special topic I would think about moving all related things to its own component.
    @GetMapping("/activities/search/{text}")
    ResponseEntity<Object> searchActivitiesByText(@PathVariable String text) {
        var searchResult = volunteerService.searchPendingActivitiesByText(text);
        if (searchResult.isEmpty()) {
            return new ResponseEntity<>("No results found for \"" + text + "\"", HttpStatus.OK);
        }
        return new ResponseEntity<>(searchResult, HttpStatus.OK);
    }

    @GetMapping("/activities/search/filtered/{text}")
    ResponseEntity<Object> searchActivitiesByTextFiltered(@PathVariable String text, @RequestBody FilterActivity filterActivity) {
        var searchResult = volunteerService.searchPendingActivitiesByTextFiltered(text, filterActivity);
        if (searchResult.isEmpty()) {
            return new ResponseEntity<>("No results found for '" + text + "'", HttpStatus.OK);
        }
        return new ResponseEntity<>(searchResult, HttpStatus.OK);
    }

    @GetMapping("/activities/list")
    List<ActivityDTO> listAllOfUsersActivities(Authentication authentication) {
        return volunteerService.listAllActivitiesOfVolunteer(authentication.getName());
    }

    @GetMapping("/webapptree")
    String viewWebAppTree(@Value("${webapptree.volunteer}") String input) {
        return input;
    }

    @PutMapping("/activities/complete/{id}/{rating}")
    ActivityDTO completeActivityVolunteer(@PathVariable Long id, @PathVariable int rating, @RequestBody String feedback, Authentication authentication){
        return activityService.completeActivityVolunteer(id, rating, feedback, authentication.getName()).orElse(null);
    }

    @PutMapping("/activities/apply/{id}")
    void applyForActivity(@PathVariable Long id, Authentication authentication){
        activityService.applyForActivity(id, authentication.getName());
    }

    @PutMapping("/activities/approve/{id}")
    void approveApplication(@PathVariable Long id, Authentication authentication){
        activityService.approveRecommendationAsVolunteer(id, authentication.getName());
    }

    @PutMapping("/activities/deny/{id}")
    void denyApplication(@PathVariable Long id, Authentication authentication){
        activityService.denyRecommendationAsVolunteer(id, authentication.getName());
    }


    @PutMapping("/activities/email/{keyword}/{schedule}")
    void registerForEmailNotificationByKeywordDailyWeeklyOrMonthly(@PathVariable String keyword, @PathVariable String schedule, Authentication authentication) {
        volunteerService.registerNewKeyword(keyword, EmailSchedule.valueOf(schedule.toUpperCase(Locale.ROOT)), authentication.getName());
    }

    @GetMapping("/activities/email/keywords")
    Map<String, EmailSchedule> viewKeywordRegistrations(Authentication authentication) {
        return volunteerService.viewKeywordRegistrations(authentication.getName());
    }

    @DeleteMapping("/activities/email/{keyword}/delete")
    void deleteKeywordRegistration(@PathVariable String keyword, Authentication authentication) {
        volunteerService.deleteKeywordRegistration(keyword, authentication.getName());
    }

    @PutMapping("/activities/delete/{id}")
    void removeApplication(@PathVariable Long id, Authentication authentication){
        activityService.deleteApplicationAsVolunteer(id, authentication.getName());
    }


}