package bugbusters.everyonecodes.java.usermanagement.rolemanagement.organization;

import bugbusters.everyonecodes.java.activities.*;
import bugbusters.everyonecodes.java.search.VolunteerTextSearchService;
import bugbusters.everyonecodes.java.usermanagement.api.UserPrivateDTO;
import bugbusters.everyonecodes.java.usermanagement.api.UserPublicDTO;
import bugbusters.everyonecodes.java.usermanagement.api.VolunteerPublicDTO;
import bugbusters.everyonecodes.java.usermanagement.api.VolunteerSearchResultDTO;
import bugbusters.everyonecodes.java.usermanagement.data.*;
import bugbusters.everyonecodes.java.usermanagement.repository.OrganizationRepository;
import bugbusters.everyonecodes.java.usermanagement.repository.VolunteerRepository;
import bugbusters.everyonecodes.java.usermanagement.service.OrganizationService;
import bugbusters.everyonecodes.java.usermanagement.service.UserDTOMapper;
import bugbusters.everyonecodes.java.usermanagement.service.UserService;
import bugbusters.everyonecodes.java.usermanagement.service.VolunteerDTOMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class OrganizationServiceTest {

    @Autowired
    OrganizationService organizationService;

    @MockBean
    VolunteerRepository volunteerRepository;

    @MockBean
    OrganizationRepository organizationRepository;

    @MockBean
    ActivityRepository activityRepository;

    @MockBean
    UserService userService;

    @MockBean
    UserDTOMapper userMapper;

    @MockBean
    VolunteerDTOMapper volunteerMapper;

    @MockBean
    VolunteerTextSearchService volunteerTextSearchService;

    @MockBean
    ActivityDTOMapper activityDTOMapper;

    // for testing
    private final String username = "test";
    private final User user = new User("test", "test", "test",
            "test", LocalDate.of(2000, 1, 1), "test",
            "test", "test");
    private final UserPrivateDTO userPrivateDTO = new UserPrivateDTO(username, user.getRole(), user.getFullName(), user.getBirthday(), user.getAddress(), user.getEmail(), user.getDescription());
    private final UserPublicDTO userPublicDTO = new UserPublicDTO(username, "test", 1, "test", 5.0, 0);
    private final Organization organization = new Organization(user);
    private final Volunteer volunteer = new Volunteer(user);
    private final VolunteerSearchResultDTO volunteerSearchResultDTO = new VolunteerSearchResultDTO("test", null, null);
    private final Activity activity = new Activity("test", "test", "test", Set.of("test"), Set.of("test"), LocalDateTime.now(), LocalDateTime.now(), true, Status.PENDING, Status.PENDING, null, null, null, null);
    private final Activity draft = new Activity("test", "test", "test", Set.of("test"), Set.of("test"), LocalDateTime.now(), LocalDateTime.now(), true, Status.DRAFT, Status.DRAFT, null, null, null, null);
    private final ActivityDTO activityDTO = new ActivityDTO("test", "test", "test", Status.PENDING, LocalDateTime.now(), LocalDateTime.now(), null, null, null, null, null, null);
    private final ActivityDTO draftDTO = new ActivityDTO("test", "test", "test", Status.DRAFT, LocalDateTime.now(), LocalDateTime.now(), null, null, null, null, null, null);

    @Test
    void getOrganizationByUsername() {
        organizationService.getOrganizationByUsername(username);
        Mockito.verify(organizationRepository).findOneByUsername(username);
    }

    @Test
    void viewOrganisationPrivateData_UserFound() {
        Mockito.when(organizationRepository.findOneByUsername(username)).thenReturn(Optional.of(organization));
        Mockito.when(userMapper.toUserPrivateDTO(organization)).thenReturn(userPrivateDTO);
        var oResult = organizationService.viewOrganisationPrivateData(username);
        Assertions.assertEquals(Optional.of(userPrivateDTO), oResult);
        Mockito.verify(organizationRepository, Mockito.times(1)).findOneByUsername(username);
        Mockito.verify(userMapper, Mockito.times(1)).toUserPrivateDTO(organization);
    }

    @Test
    void viewOrganisationPrivateData_UserNotFound() {
        Mockito.when(organizationRepository.findOneByUsername(username)).thenReturn(Optional.empty());
        var oResult = organizationService.viewOrganisationPrivateData(username);
        Assertions.assertEquals(Optional.empty(), oResult);
        Mockito.verify(organizationRepository, Mockito.times(1)).findOneByUsername(username);
        Mockito.verify(userMapper, Mockito.never()).toUserPrivateDTO(Mockito.any(Individual.class));
    }

    @Test
    void viewOrganisationPublicData_UserFound() {
        Mockito.when(organizationRepository.findOneByUsername(username)).thenReturn(Optional.of(organization));
        Mockito.when(userMapper.toUserPublicDTO(organization)).thenReturn(userPublicDTO);
        var oResult = organizationService.viewOrganisationPublicData(username);
        Assertions.assertEquals(Optional.of(userPublicDTO), oResult);
        Mockito.verify(organizationRepository, Mockito.times(1)).findOneByUsername(username);
        Mockito.verify(userMapper, Mockito.times(1)).toUserPublicDTO(organization);
    }

    @Test
    void viewOrganisationPublicData_UserNotFound() {
        Mockito.when(organizationRepository.findOneByUsername(username)).thenReturn(Optional.empty());
        var oResult = organizationService.viewOrganisationPublicData(username);
        Assertions.assertEquals(Optional.empty(), oResult);
        Mockito.verify(organizationRepository, Mockito.times(1)).findOneByUsername(username);
        Mockito.verify(userMapper, Mockito.never()).toUserPublicDTO(Mockito.any(Individual.class));
    }

    @Test
    void viewVolunteerPublicData_UserFound() {
        Mockito.when(volunteerRepository.findOneByUsername(username)).thenReturn(Optional.of(volunteer));
        Mockito.when(volunteerMapper.toVolunteerPublicDTO(volunteer)).thenReturn(new VolunteerPublicDTO(userPublicDTO.getUsername(), "skills"));
        var oResult = organizationService.viewVolunteerPublicData(username);
        Assertions.assertEquals(Optional.of(new VolunteerPublicDTO(userPublicDTO.getUsername(), "skills")), oResult);
        Mockito.verify(volunteerRepository, Mockito.times(1)).findOneByUsername(username);
        Mockito.verify(volunteerMapper, Mockito.times(1)).toVolunteerPublicDTO(volunteer);
    }

    @Test
    void viewVolunteerPublicData_UserNotFound() {
        Mockito.when(volunteerRepository.findOneByUsername(username)).thenReturn(Optional.empty());
        var oResult = organizationService.viewVolunteerPublicData(username);
        Assertions.assertEquals(Optional.empty(), oResult);
        Mockito.verify(volunteerRepository, Mockito.times(1)).findOneByUsername(username);
        Mockito.verify(volunteerMapper, Mockito.never()).toVolunteerPublicDTO(Mockito.any(Volunteer.class));
    }

    @Test
    void editOrganizationData_DataFound() {
        Mockito.when(organizationRepository.findOneByUsername(username)).thenReturn(Optional.of(organization));
        Mockito.when(organizationRepository.save(organization)).thenReturn(organization);
        Mockito.when(userMapper.toUserPrivateDTO(organization)).thenReturn(userPrivateDTO);
        var oResult = organizationService.editOrganizationData(userPrivateDTO, username);
        Mockito.verify(userService, Mockito.times(1)).editUserData(userPrivateDTO, username);
        Mockito.verify(organizationRepository, Mockito.times(1)).save(Mockito.any(Organization.class));
        Assertions.assertTrue(oResult.isPresent());
    }

    @Test
    void editOrganizationData_Empty() {
        Mockito.when(organizationRepository.findOneByUsername(username)).thenReturn(Optional.empty());
        var oResult = organizationService.editOrganizationData(userPrivateDTO, username);
        Assertions.assertEquals(Optional.empty(), oResult);
        Mockito.verify(organizationRepository, Mockito.never()).save(Mockito.any(Organization.class));
    }

    @Test
    void listAllVolunteers() {
        Mockito.when(volunteerRepository.findAll()).thenReturn(List.of(volunteer));
        Mockito.when(volunteerMapper.toVolunteerSearchResultDTO(volunteer)).thenReturn(volunteerSearchResultDTO);
        var result = organizationService.listAllVolunteers();
        Assertions.assertEquals(List.of(volunteerSearchResultDTO), result);
    }

    @Test
    void listAllVolunteers_Empty() {
        Mockito.when(volunteerRepository.findAll()).thenReturn(List.of());
        var result = organizationService.listAllVolunteers();
        Assertions.assertEquals(List.of(), result);
    }


    @Test
    void searchVolunteersByText() {
        Mockito.when(volunteerRepository.findAll()).thenReturn(List.of(volunteer));
        Mockito.when(volunteerTextSearchService.searchVolunteersByText(List.of(volunteer), "test")).thenReturn(List.of(volunteer));
        Mockito.when(volunteerMapper.toVolunteerSearchResultDTO(volunteer)).thenReturn(volunteerSearchResultDTO);
        var result = organizationService.searchVolunteersByText("test");
        Assertions.assertEquals(List.of(volunteerSearchResultDTO), result);
    }

    @Test
    void searchVolunteersByText_Empty() {
        Mockito.when(volunteerRepository.findAll()).thenReturn(List.of(volunteer));
        Mockito.when(volunteerTextSearchService.searchVolunteersByText(List.of(volunteer), "test")).thenReturn(List.of());
        var result = organizationService.searchVolunteersByText("test");
        Assertions.assertEquals(List.of(), result);
    }

    @Test
    void listAllActivitiesOfOrganization() {
        Mockito.when(activityRepository.findAllByCreator(username)).thenReturn(List.of(activity, draft));
        Mockito.when(activityDTOMapper.toClientActivityDTO(activity)).thenReturn(activityDTO);
        Mockito.when(activityDTOMapper.toClientActivityDTO(draft)).thenReturn(draftDTO);
        var result = organizationService.listAllActivitiesOfOrganization(username);
        Assertions.assertEquals(List.of(activityDTO, draftDTO), result);
        Mockito.verify(activityRepository).findAllByCreator(username);
    }

    @Test
    void listAllActivitiesOfOrganization_Empty() {
        Mockito.when(activityRepository.findAllByCreator(username)).thenReturn(List.of());
        Mockito.when(activityDTOMapper.toClientActivityDTO(Mockito.any(Activity.class))).thenReturn(activityDTO);
        var result = organizationService.listAllActivitiesOfOrganization(username);
        Assertions.assertEquals(List.of(), result);
        Mockito.verify(activityRepository).findAllByCreator(username);
    }

    @Test
    void listAllDraftsOfOrganization() {
        Mockito.when(activityRepository.findAllByCreatorAndStatusClient(username, Status.DRAFT)).thenReturn(List.of(draft));
        Mockito.when(activityDTOMapper.toClientActivityDTO(draft)).thenReturn(draftDTO);
        var result = organizationService.listAllDraftsOfOrganization(username);
        Assertions.assertEquals(List.of(draftDTO), result);
        Mockito.verify(activityRepository).findAllByCreatorAndStatusClient(username, Status.DRAFT);
    }

    @Test
    void listAllDraftsOfOrganization_Empty() {
        Mockito.when(activityRepository.findAllByCreatorAndStatusClient(username, Status.DRAFT)).thenReturn(List.of());
        Mockito.when(activityDTOMapper.toClientActivityDTO(Mockito.any(Activity.class))).thenReturn(draftDTO);
        var result = organizationService.listAllDraftsOfOrganization(username);
        Assertions.assertEquals(List.of(), result);
        Mockito.verify(activityRepository).findAllByCreatorAndStatusClient(username, Status.DRAFT);
    }
}