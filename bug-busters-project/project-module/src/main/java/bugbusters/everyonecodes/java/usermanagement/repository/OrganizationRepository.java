package bugbusters.everyonecodes.java.usermanagement.repository;

import bugbusters.everyonecodes.java.usermanagement.data.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findOneByUsername(String username);

}
