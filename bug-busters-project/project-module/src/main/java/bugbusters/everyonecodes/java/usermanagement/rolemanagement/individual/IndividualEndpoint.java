package bugbusters.everyonecodes.java.usermanagement.rolemanagement.individual;

import bugbusters.everyonecodes.java.usermanagement.rolemanagement.organization.ClientPrivateDTO;
import bugbusters.everyonecodes.java.usermanagement.rolemanagement.organization.ClientPublicDTO;
import bugbusters.everyonecodes.java.usermanagement.rolemanagement.volunteer.VolunteerPublicDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/individual")
public class IndividualEndpoint {
    private final IndividualService individualService;

    public IndividualEndpoint(IndividualService individualService) {
        this.individualService = individualService;
    }


    @GetMapping("/login")
    ClientPrivateDTO viewIndividualData(Authentication authentication) {
        return individualService.viewIndividualPrivateData(authentication.getName()).orElse(null);
    }

    @PutMapping("/edit")
    ClientPrivateDTO editIndividualData(@Valid @RequestBody ClientPrivateDTO edits, Authentication authentication) {
        return individualService.editIndividualData(edits, authentication.getName()).orElse(null);
    }

    @GetMapping("/view")
    ClientPublicDTO viewIndividualProfile(Authentication authentication) {
        return individualService.viewIndividualPublicData(authentication.getName()).orElse(null);
    }

    @GetMapping("/view/{username}")
    VolunteerPublicDTO viewVolunteerProfile(@PathVariable String username) {
        return individualService.viewVolunteerPublicData(username).orElse(null);
    }
}