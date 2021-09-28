package bugbusters.everyonecodes.java.usermanagement.service;

import bugbusters.everyonecodes.java.activities.ActivityDTO;
import bugbusters.everyonecodes.java.activities.ActivityDTOMapper;
import bugbusters.everyonecodes.java.activities.ActivityRepository;
import bugbusters.everyonecodes.java.activities.Status;
import bugbusters.everyonecodes.java.search.FilterVolunteer;
import bugbusters.everyonecodes.java.search.FilterVolunteerService;
import bugbusters.everyonecodes.java.search.VolunteerTextSearchService;
import bugbusters.everyonecodes.java.usermanagement.data.*;
import bugbusters.everyonecodes.java.usermanagement.repository.IndividualRepository;
import bugbusters.everyonecodes.java.usermanagement.repository.VolunteerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IndividualService {
    private final VolunteerRepository volunteerRepository;
    private final IndividualRepository individualRepository;
    private final UserService userService;
    private final UserDTOMapper userMapper;
    private final VolunteerDTOMapper volunteerMapper;
    private final VolunteerTextSearchService volunteerTextSearchService;
    private final ActivityDTOMapper activityDTOMapper;
    private final ActivityRepository activityRepository;
    private final FilterVolunteerService filterVolunteerService;


    public IndividualService(VolunteerRepository volunteerRepository, IndividualRepository individualRepository, UserService userService, UserDTOMapper userMapper, VolunteerDTOMapper volunteerMapper, VolunteerTextSearchService volunteerTextSearchService, ActivityDTOMapper activityDTOMapper, ActivityRepository activityRepository, FilterVolunteerService filterVolunteerService) {
        this.volunteerRepository = volunteerRepository;
        this.individualRepository = individualRepository;
        this.userService = userService;
        this.userMapper = userMapper;
        this.volunteerMapper = volunteerMapper;
        this.volunteerTextSearchService = volunteerTextSearchService;
        this.activityDTOMapper = activityDTOMapper;
        this.activityRepository = activityRepository;
        this.filterVolunteerService = filterVolunteerService;
    }

    public Optional<UserPrivateDTO> editIndividualData(UserPrivateDTO input, String username) {
        Optional<Individual> optionalIndividual = getIndividualByUsername(username);
        if(optionalIndividual.isEmpty()) return Optional.empty();
        userService.editUserData(input, username);
        Individual individual = getIndividualByUsername(username).get();
        //edit properties here
        individual = individualRepository.save(individual);
        return Optional.of(userMapper.toUserPrivateDTO(individual));
    }

    public Optional<Individual> getIndividualByUsername(String username) {
        return individualRepository.findOneByUsername(username);
    }

    public Optional<UserPrivateDTO> viewIndividualPrivateData(String username) {
        return getIndividualByUsername(username).map(organization -> userMapper.toUserPrivateDTO(organization));
    }

    public Optional<UserPublicDTO> viewIndividualPublicData(String username) {
        return getIndividualByUsername(username).map(organization -> userMapper.toUserPublicDTO(organization));
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

    public List<ActivityDTO> listAllActivitiesOfIndividual(String username) {
        var result = activityRepository.findAllByCreator(username);
        return result.stream()
                .map(activity -> activityDTOMapper.toClientActivityDTO(activity))
                .collect(Collectors.toList());
    }

    public List<ActivityDTO> listAllDraftsOfIndividual(String username) {
        var result = activityRepository.findAllByCreatorAndStatusClient(username, Status.DRAFT);
        return result.stream()
                .map(activity -> activityDTOMapper.toClientActivityDTO(activity))
                .collect(Collectors.toList());
    }
}
