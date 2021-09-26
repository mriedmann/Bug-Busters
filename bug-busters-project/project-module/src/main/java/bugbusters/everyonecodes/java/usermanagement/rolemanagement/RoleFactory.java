package bugbusters.everyonecodes.java.usermanagement.rolemanagement;

import bugbusters.everyonecodes.java.usermanagement.data.User;
import bugbusters.everyonecodes.java.usermanagement.rolemanagement.individual.Individual;
import bugbusters.everyonecodes.java.usermanagement.rolemanagement.individual.IndividualRepository;
import bugbusters.everyonecodes.java.usermanagement.rolemanagement.organization.Organization;
import bugbusters.everyonecodes.java.usermanagement.rolemanagement.organization.OrganizationRepository;
import bugbusters.everyonecodes.java.usermanagement.rolemanagement.volunteer.Volunteer;
import bugbusters.everyonecodes.java.usermanagement.rolemanagement.volunteer.VolunteerRepository;
import org.springframework.stereotype.Service;

// REVIEW: All classes of the rolemanagement package seems like they should rather be in usermanagement.
//         What was the reason to put it inside its own package?
@Service
public class RoleFactory {

    private final VolunteerRepository volunteerRepository;
    private final OrganizationRepository organizationRepository;
    private final IndividualRepository individualRepository;

    public RoleFactory(VolunteerRepository volunteerRepository, OrganizationRepository organizationRepository, IndividualRepository individualRepository) {
        this.volunteerRepository = volunteerRepository;
        this.organizationRepository = organizationRepository;
        this.individualRepository = individualRepository;
    }

    // REVIEW: I am honest with you, this looks bad, but I can't really pin it down.
    //         First simple thing: I think that this is no correct implementation of the factory pattern.
    //         Also, saving an object to database on factory.create is nothing that I would expect. Normally we use
    //         Factories to only create objects, but not saving them. I think I understand your problem here and your
    //         solution is fascinating. Maybe we can find a more "conventional" solution together.
    //         Just as a reminder for myself: It really looks like you could have used a polymorphic approach instead.
    public void createRole(User user) {
        switch (user.getRole()) {
            case "ROLE_VOLUNTEER" -> volunteerRepository.save(new Volunteer(user));
            case "ROLE_ORGANIZATION" -> organizationRepository.save(new Organization(user));
            case "ROLE_INDIVIDUAL" -> individualRepository.save(new Individual(user));
            default -> throw new IllegalArgumentException();
        }
    }
}
