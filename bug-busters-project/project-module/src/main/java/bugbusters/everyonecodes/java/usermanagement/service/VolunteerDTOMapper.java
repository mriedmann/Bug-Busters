package bugbusters.everyonecodes.java.usermanagement.service;

import bugbusters.everyonecodes.java.usermanagement.data.Volunteer;
import bugbusters.everyonecodes.java.usermanagement.data.VolunteerPrivateDTO;
import bugbusters.everyonecodes.java.usermanagement.data.VolunteerPublicDTO;
import bugbusters.everyonecodes.java.usermanagement.data.VolunteerSearchResultDTO;
import org.springframework.stereotype.Service;

@Service
public class VolunteerDTOMapper {
    private final UserDTOMapper userDTOMapper;
    private final SetToStringMapper setToStringMapper;

    public VolunteerDTOMapper(UserDTOMapper userDTOMapper, SetToStringMapper setToStringMapper) {
        this.userDTOMapper = userDTOMapper;
        this.setToStringMapper = setToStringMapper;
    }

    public VolunteerPrivateDTO toVolunteerPrivateDTO(Volunteer input) {
        var skills = setToStringMapper.convertToString(input.getSkills());
        return new VolunteerPrivateDTO(input.getUsername(), skills);
    }

    public VolunteerPublicDTO toVolunteerPublicDTO(Volunteer input) {
        var skills = setToStringMapper.convertToString(input.getSkills());
        return new VolunteerPublicDTO(input.getUsername(), skills);
    }

    public VolunteerSearchResultDTO toVolunteerSearchResultDTO(Volunteer input) {
        return new VolunteerSearchResultDTO(input.getUsername(),
                setToStringMapper.convertToString(input.getSkills()),
                RatingCalcUtil.calculateRating(input.getRatings()));
    }
}
