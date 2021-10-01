package bugbusters.everyonecodes.java.usermanagement.rolemanagement.volunteer;

import bugbusters.everyonecodes.java.activities.*;
import bugbusters.everyonecodes.java.search.ActivityTextSearchService;
import bugbusters.everyonecodes.java.usermanagement.api.UserPrivateDTO;
import bugbusters.everyonecodes.java.usermanagement.api.UserPublicDTO;
import bugbusters.everyonecodes.java.usermanagement.api.VolunteerPrivateDTO;
import bugbusters.everyonecodes.java.usermanagement.api.VolunteerPublicDTO;
import bugbusters.everyonecodes.java.usermanagement.data.*;
import bugbusters.everyonecodes.java.usermanagement.repository.VolunteerRepository;
import bugbusters.everyonecodes.java.usermanagement.repository.IndividualRepository;
import bugbusters.everyonecodes.java.usermanagement.repository.OrganizationRepository;
import bugbusters.everyonecodes.java.usermanagement.service.UserDTOMapper;
import bugbusters.everyonecodes.java.usermanagement.service.UserService;
import bugbusters.everyonecodes.java.usermanagement.service.VolunteerDTOMapper;
import bugbusters.everyonecodes.java.usermanagement.service.VolunteerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static bugbusters.everyonecodes.java.usermanagement.data.EmailSchedule.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class VolunteerServiceTest {

    @Autowired
    VolunteerService volunteerService;

    @MockBean
    VolunteerRepository volunteerRepository;

    @MockBean
    OrganizationRepository organizationRepository;

    @MockBean
    IndividualRepository individualRepository;

    @MockBean
    ActivityRepository activityRepository;

    @MockBean
    UserService userService;

    @MockBean
    UserDTOMapper userMapper;

    @MockBean
    VolunteerDTOMapper volunteerMapper;

    @MockBean
    ActivityTextSearchService activityTextSearchService;

    @MockBean
    ActivityDTOMapper activityDTOMapper;

    // for testing

    private String username;
    private User user;
    private UserPrivateDTO userPrivateDTO;
    private UserPublicDTO userPublicDTO;
    private Individual individual;
    private Organization organization;
    private Volunteer volunteer;
    private Activity activity;
    private ActivityDTO activityDTO;

    @BeforeEach
    void setUp() {
        username = "test";
        user = new User("test", "test", "test",
                "test", LocalDate.of(2000, 1, 1), "test",
                "test", "test");
        userPrivateDTO = new UserPrivateDTO(username, user.getRole(), user.getFullName(), user.getBirthday(), user.getAddress(), user.getEmail(), user.getDescription());
        userPublicDTO = new UserPublicDTO(username, "test", 1, "test", 5.0, 0);
        individual = new Individual(user);
        organization = new Organization(user);
        volunteer = new Volunteer(user);
        activity = new Activity("test", "test", "test", Set.of("test"), Set.of("test"), LocalDateTime.now(), LocalDateTime.now(), true, Status.PENDING, Status.PENDING, null, null, null, null);
        activityDTO = new ActivityDTO("test", "test", "test", Status.PENDING, LocalDateTime.now(), LocalDateTime.now(), null, null, null, null, null, null);
    }


    @Test
    void getVolunteerByUsername() {
        volunteerService.getVolunteerByUsername(username);
        Mockito.verify(volunteerRepository).findOneByUsername(username);
    }

    @Test
    void viewVolunteerPrivateData_UserFound() {
        Mockito.when(volunteerRepository.findOneByUsername(username)).thenReturn(Optional.of(volunteer));
        Mockito.when(volunteerMapper.toVolunteerPrivateDTO(volunteer)).thenReturn(new VolunteerPrivateDTO(userPrivateDTO.getUsername(), null));
        var oResult = volunteerService.viewVolunteerPrivateData(username);
        Assertions.assertEquals(Optional.of(new VolunteerPrivateDTO(userPrivateDTO.getUsername(), null)), oResult);
        Mockito.verify(volunteerRepository, Mockito.times(1)).findOneByUsername(username);
        Mockito.verify(volunteerMapper, Mockito.times(1)).toVolunteerPrivateDTO(volunteer);
    }

    @Test
    void viewVolunteerPrivateData_UserNotFound() {
        Mockito.when(volunteerRepository.findOneByUsername(username)).thenReturn(Optional.empty());
        var oResult = volunteerService.viewVolunteerPrivateData(username);
        Assertions.assertEquals(Optional.empty(), oResult);
        Mockito.verify(volunteerRepository, Mockito.times(1)).findOneByUsername(username);
        Mockito.verify(volunteerMapper, Mockito.never()).toVolunteerPrivateDTO(Mockito.any(Volunteer.class));
    }

    @Test
    void viewVolunteerPublicData_UserFound() {
        Mockito.when(volunteerRepository.findOneByUsername(username)).thenReturn(Optional.of(volunteer));
        Mockito.when(volunteerMapper.toVolunteerPublicDTO(volunteer)).thenReturn(new VolunteerPublicDTO(userPublicDTO.getUsername(), null));
        var oResult = volunteerService.viewVolunteerPublicData(username);
        Assertions.assertEquals(Optional.of(new VolunteerPublicDTO(userPublicDTO.getUsername(), null)), oResult);
        Mockito.verify(volunteerRepository, Mockito.times(1)).findOneByUsername(username);
        Mockito.verify(volunteerMapper, Mockito.times(1)).toVolunteerPublicDTO(volunteer);
    }

    @Test
    void viewVolunteerPublicData_UserNotFound() {
        Mockito.when(volunteerRepository.findOneByUsername(username)).thenReturn(Optional.empty());
        var oResult = volunteerService.viewVolunteerPublicData(username);
        Assertions.assertEquals(Optional.empty(), oResult);
        Mockito.verify(volunteerRepository, Mockito.times(1)).findOneByUsername(username);
        Mockito.verify(volunteerMapper, Mockito.never()).toVolunteerPrivateDTO(Mockito.any(Volunteer.class));
    }

    @Test //this tests getClientByUsername private helper method as well
    void viewClientPublicData_OrganizationFound() {
        Mockito.when(organizationRepository.findOneByUsername(username)).thenReturn(Optional.of(organization));
        Mockito.when(userMapper.toUserPublicDTO(organization)).thenReturn(userPublicDTO);
        var oResult = volunteerService.viewClientPublicData(username);
        Assertions.assertEquals(Optional.of(userPublicDTO), oResult);
        Mockito.verify(organizationRepository, Mockito.times(1)).findOneByUsername(username);
        Mockito.verify(individualRepository, Mockito.never()).findOneByUsername(username);
        Mockito.verify(userMapper, Mockito.times(1)).toUserPublicDTO(organization);
    }

    @Test
    void viewClientPublicData_IndividualFound() {
        Mockito.when(organizationRepository.findOneByUsername(username)).thenReturn(Optional.empty());
        Mockito.when(individualRepository.findOneByUsername(username)).thenReturn(Optional.of(individual));
        Mockito.when(userMapper.toUserPublicDTO(individual)).thenReturn(userPublicDTO);
        var oResult = volunteerService.viewClientPublicData(username);
        Assertions.assertEquals(Optional.of(userPublicDTO), oResult);
        Mockito.verify(organizationRepository, Mockito.times(1)).findOneByUsername(username);
        Mockito.verify(individualRepository, Mockito.times(1)).findOneByUsername(username);
        Mockito.verify(userMapper, Mockito.times(1)).toUserPublicDTO(individual);
    }

    @Test
    void viewClientPublicData_UserNotFound() {
        Mockito.when(individualRepository.findOneByUsername(username)).thenReturn(Optional.empty());
        var oResult = volunteerService.viewClientPublicData(username);
        Assertions.assertEquals(Optional.empty(), oResult);
        Mockito.verify(individualRepository, Mockito.times(1)).findOneByUsername(username);
        Mockito.verify(userMapper, Mockito.never()).toUserPublicDTO(Mockito.any(Individual.class));
    }

    @Test
    void editVolunteerData_DataFound() {
        VolunteerPrivateDTO volunteerPrivateDTO = new VolunteerPrivateDTO(userPrivateDTO.getUsername(), null);
        Mockito.when(volunteerRepository.findOneByUsername(username)).thenReturn(Optional.of(volunteer));
        Mockito.when(volunteerRepository.save(volunteer)).thenReturn(volunteer);
        Mockito.when(volunteerMapper.toVolunteerPrivateDTO(volunteer)).thenReturn(volunteerPrivateDTO);
        var oResult = volunteerService.editVolunteerData(volunteerPrivateDTO);
        Mockito.verify(volunteerRepository, Mockito.times(1)).save(Mockito.any(Volunteer.class));
        Assertions.assertTrue(oResult.isPresent());
    }

    @Test
    void editVolunteerData_Empty() {
        Mockito.when(volunteerRepository.findOneByUsername(username)).thenReturn(Optional.empty());
        var oResult = volunteerService.editVolunteerData(new VolunteerPrivateDTO(userPrivateDTO.getUsername(), null));
        Assertions.assertEquals(Optional.empty(), oResult);
        Mockito.verify(volunteerRepository, Mockito.never()).save(Mockito.any(Volunteer.class));
    }

    @Test
    void listPendingActivities() {
        Mockito.when(activityRepository.findAllByStatusClient(Status.PENDING)).thenReturn(List.of(activity));
        Mockito.when(activityDTOMapper.toVolunteerActivityDTO(activity)).thenReturn(activityDTO);
        var result = volunteerService.listAllPendingActivities();
        Assertions.assertEquals(List.of(activityDTO), result);
    }

    @Test
    void listPendingActivities_Empty() {
        Mockito.when(activityRepository.findAllByStatusClient(Status.PENDING)).thenReturn(List.of());
        var result = volunteerService.listAllPendingActivities();
        Assertions.assertEquals(List.of(), result);
    }

    @Test
    void searchPendingActivitiesByText() {
        Mockito.when(activityRepository.findAllByStatusClient(Status.PENDING)).thenReturn(List.of(activity));
        Mockito.when(activityTextSearchService.searchActivitiesByText(List.of(activity), "test")).thenReturn(List.of(activity));
        Mockito.when(activityDTOMapper.toVolunteerActivityDTO(activity)).thenReturn(activityDTO);
        var result = volunteerService.searchPendingActivitiesByText("test");
        Assertions.assertEquals(List.of(activityDTO), result);
    }

    @Test
    void searchPendingActivitiesByText_Empty() {
        Mockito.when(activityTextSearchService.searchActivitiesByText(List.of(activity), "test")).thenReturn(List.of());
        var result = volunteerService.searchPendingActivitiesByText("test");
        Assertions.assertEquals(List.of(), result);
    }

    @Test
    void listAllActivitiesOfVolunteer() {
        volunteer.setActivities(List.of(activity));
        Mockito.when(volunteerRepository.findOneByUsername("test")).thenReturn(Optional.of(volunteer));
        Mockito.when(activityDTOMapper.toVolunteerActivityDTO(activity)).thenReturn(activityDTO);
        var result = volunteerService.listAllActivitiesOfVolunteer("test");
        Assertions.assertEquals(List.of(activityDTO), result);
    }

    @Test
    void listAllActivitiesOfVolunteer_Empty() {
        volunteer.setActivities(List.of(activity));
        Mockito.when(volunteerRepository.findOneByUsername("test")).thenReturn(Optional.empty());
        var result = volunteerService.listAllActivitiesOfVolunteer("test");
        Assertions.assertEquals(List.of(), result);
    }

    // registerNewKeyword() Test
    @Test
    void registerNewKeyword() {
        Mockito.when(volunteerRepository.findOneByUsername("test")).thenReturn(Optional.of(volunteer));
        volunteer.setRegisteredKeywords(new HashMap<>());
        volunteerService.registerNewKeyword("cook", DAILY, "test");
        var result = volunteer.getRegisteredKeywords();
        Assertions.assertEquals(Map.of("cook", DAILY), result);
    }

    @Test
    void registerNewKeyword_EmailScheduleNONE() {
        Mockito.when(volunteerRepository.findOneByUsername("test")).thenReturn(Optional.of(volunteer));
        volunteer.setRegisteredKeywords(new HashMap<>());
        volunteerService.registerNewKeyword("cook", NONE, "test");
        var result = volunteer.getRegisteredKeywords();
        Assertions.assertEquals(Map.of(), result);
    }

    //TODO: sendEmailsForMatchingKeywords() Test
    @Test
    void sendEmailsForMatchingKeywords() {

    }


    // viewKeywordRegistrations() Test
    @Test
    void viewKeywordRegistrations() {
        Mockito.when(volunteerRepository.findOneByUsername("test")).thenReturn(Optional.of(volunteer));
        volunteer.setRegisteredKeywords(new HashMap<>(Map.of("cook", DAILY)));
        var expected = volunteer.getRegisteredKeywords();
        var result = volunteerService.viewKeywordRegistrations("test");
        Assertions.assertEquals(expected, result);
    }

    @Test
    void viewKeywordRegistrations_EmptyMap() {
        Mockito.when(volunteerRepository.findOneByUsername("test")).thenReturn(Optional.of(volunteer));
        volunteer.setRegisteredKeywords(new HashMap<>());
        var expected = volunteer.getRegisteredKeywords();
        var result = volunteerService.viewKeywordRegistrations("test");
        Assertions.assertEquals(expected, result);
    }

    @Test
    void deleteKeywordRegistration() {
        Mockito.when(volunteerRepository.findOneByUsername(username)).thenReturn(Optional.of(volunteer));
        volunteer.setRegisteredKeywords(new HashMap<>(Map.of("cook", DAILY, "garden", WEEKLY)));
        volunteerService.deleteKeywordRegistration("cook", username);
        var result = volunteer.getRegisteredKeywords();
        Assertions.assertEquals(Map.of("garden", WEEKLY), result);
    }

    @Test
    void deleteKeywordRegistration_removeNoEntryIfNoMatch() {
        Mockito.when(volunteerRepository.findOneByUsername(username)).thenReturn(Optional.of(volunteer));
        volunteer.setRegisteredKeywords(new HashMap<>(Map.of("cook", DAILY, "garden", WEEKLY)));
        volunteerService.deleteKeywordRegistration("pilot", username);
        var result = volunteer.getRegisteredKeywords();
        Assertions.assertEquals(Map.of("garden", WEEKLY, "cook", DAILY), result);
    }

}