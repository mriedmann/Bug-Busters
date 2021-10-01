package bugbusters.everyonecodes.java.usermanagement.service;

import bugbusters.everyonecodes.java.activities.ActivityDTO;
import bugbusters.everyonecodes.java.activities.ActivityDTOMapper;
import bugbusters.everyonecodes.java.activities.ActivityRepository;
import bugbusters.everyonecodes.java.activities.Status;
import bugbusters.everyonecodes.java.search.FilterVolunteer;
import bugbusters.everyonecodes.java.search.FilterVolunteerService;
import bugbusters.everyonecodes.java.search.VolunteerTextSearchService;
import bugbusters.everyonecodes.java.usermanagement.api.UserPrivateDTO;
import bugbusters.everyonecodes.java.usermanagement.api.UserPublicDTO;
import bugbusters.everyonecodes.java.usermanagement.api.VolunteerPublicDTO;
import bugbusters.everyonecodes.java.usermanagement.api.VolunteerSearchResultDTO;
import bugbusters.everyonecodes.java.usermanagement.data.*;
import bugbusters.everyonecodes.java.usermanagement.repository.OrganizationRepository;
import bugbusters.everyonecodes.java.usermanagement.repository.VolunteerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserService userService;
    private final VolunteerRepository volunteerRepository;
    private final UserDTOMapper userMapper;
    private final VolunteerDTOMapper volunteerMapper;
    private final VolunteerTextSearchService volunteerTextSearchService;
    private final ActivityDTOMapper activityDTOMapper;
    private final ActivityRepository activityRepository;
    private final FilterVolunteerService filterVolunteerService;

    public OrganizationService(OrganizationRepository organizationRepository, UserService userService, VolunteerRepository volunteerRepository, UserDTOMapper userMapper, VolunteerDTOMapper volunteerMapper, VolunteerTextSearchService volunteerTextSearchService, ActivityDTOMapper activityDTOMapper, ActivityRepository activityRepository, FilterVolunteerService filterVolunteerService) {
        this.organizationRepository = organizationRepository;
        this.userService = userService;
        this.volunteerRepository = volunteerRepository;
        this.userMapper = userMapper;
        this.volunteerMapper = volunteerMapper;
        this.volunteerTextSearchService = volunteerTextSearchService;
        this.activityDTOMapper = activityDTOMapper;
        this.activityRepository = activityRepository;
        this.filterVolunteerService = filterVolunteerService;
    }

    public Optional<UserPrivateDTO> editOrganizationData(UserPrivateDTO input, String username) {
        Optional<Organization> oOrganization = getOrganizationByUsername(username);
        if(oOrganization.isEmpty()) return Optional.empty();
        userService.editUserData(input, username);
        Organization organization = getOrganizationByUsername(username).get();
        //edit properties here
        organization = organizationRepository.save(organization);
        return Optional.of(userMapper.toUserPrivateDTO(organization));
    }

    public Optional<Organization> getOrganizationByUsername(String username) {
        return organizationRepository.findOneByUsername(username);
    }

    public Optional<UserPrivateDTO> viewOrganisationPrivateData(String username) {
        return getOrganizationByUsername(username).map(organization -> userMapper.toUserPrivateDTO(organization));
    }

    public Optional<UserPublicDTO> viewOrganisationPublicData(String username) {
        return getOrganizationByUsername(username).map(organization -> userMapper.toUserPublicDTO(organization));
    }

    public Optional<VolunteerPublicDTO> viewVolunteerPublicData(String username) {
        return volunteerRepository.findOneByUsername(username).map(volunteer -> volunteerMapper.toVolunteerPublicDTO(volunteer));
    }

    public List<VolunteerSearchResultDTO> listAllVolunteers() {
        return volunteerRepository.findAll().stream()
                .map(volunteer -> volunteerMapper.toVolunteerSearchResultDTO(volunteer))
                .collect(Collectors.toList());
    }

    public List<VolunteerSearchResultDTO> searchVolunteersByText(String text) {
        List<Volunteer> filteredList = volunteerTextSearchService.searchVolunteersByText(volunteerRepository.findAll(), text);
        return filteredList.stream()
                .map(volunteer -> volunteerMapper.toVolunteerSearchResultDTO(volunteer))
                .collect(Collectors.toList());
    }

    public List<VolunteerSearchResultDTO> searchVolunteersByTextFiltered(String text, FilterVolunteer filterVolunteer){
        List<Volunteer> filteredList = volunteerTextSearchService.searchVolunteersByText(volunteerRepository.findAll(), text);
        filteredList = filterVolunteerService.filterSearchResults(filteredList, filterVolunteer);
        return filteredList.stream()
                .map(volunteer -> volunteerMapper.toVolunteerSearchResultDTO(volunteer))
                .collect(Collectors.toList());
    }

    public List<ActivityDTO> listAllActivitiesOfOrganization(String username) {
        var result = activityRepository.findAllByCreator(username);
        return result.stream()
                .map(activity -> activityDTOMapper.toClientActivityDTO(activity))
                .collect(Collectors.toList());
    }

    //ToDO: Tests
    public List<ActivityDTO> listAllDraftsOfOrganization(String username) {
        var result = activityRepository.findAllByCreatorAndStatusClient(username, Status.DRAFT);
        return result.stream()
                .map(activity -> activityDTOMapper.toClientActivityDTO(activity))
                .collect(Collectors.toList());
    }

}
