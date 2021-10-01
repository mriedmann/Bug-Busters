package bugbusters.everyonecodes.java.usermanagement.rolemanagement.admin;

import bugbusters.everyonecodes.java.usermanagement.api.AdminDTO;
import bugbusters.everyonecodes.java.usermanagement.data.*;
import bugbusters.everyonecodes.java.usermanagement.repository.IndividualRepository;
import bugbusters.everyonecodes.java.usermanagement.repository.OrganizationRepository;
import bugbusters.everyonecodes.java.usermanagement.repository.VolunteerRepository;
import bugbusters.everyonecodes.java.usermanagement.service.AdminDTOMapper;
import bugbusters.everyonecodes.java.usermanagement.service.AdminService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AdminServiceTest {

    @Autowired
    AdminService service;

    @MockBean
    VolunteerRepository volunteerRepository;

    @MockBean
    OrganizationRepository organizationRepository;

    @MockBean
    IndividualRepository individualRepository;

    @MockBean
    AdminDTOMapper mapper;

    private final User testUser = new User(
            "test", "", "test", "test",
            LocalDate.parse("2000-01-01"), "test", "test", "test");

    private final AdminDTO testAdminDTO = new AdminDTO("test", null, 0, 0,0);

    @Test
    void listAllVolunteers() {
        Volunteer testVolunteer = new Volunteer(testUser);
        Mockito.when(volunteerRepository.findAll()).thenReturn(List.of(testVolunteer));
        Mockito.when(mapper.toAdminDTO(testVolunteer)).thenReturn(testAdminDTO);
        List<AdminDTO> result = service.listAllVolunteers();
        Assertions.assertEquals(List.of(testAdminDTO), result);
        Mockito.verify(volunteerRepository).findAll();
        Mockito.verify(mapper).toAdminDTO(testVolunteer);
    }

    @Test
    void listAllVolunteers_emptyList() {
        Mockito.when(volunteerRepository.findAll()).thenReturn(List.of());
        List<AdminDTO> result = service.listAllVolunteers();
        Assertions.assertEquals(List.of(), result);
        Mockito.verify(volunteerRepository).findAll();
        Mockito.verify(mapper, Mockito.never()).toAdminDTO(new User());
    }

    @Test
    void listAllOrganizations() {
        Organization testOrganization = new Organization(testUser);
        Mockito.when(organizationRepository.findAll()).thenReturn(List.of(testOrganization));
        Mockito.when(mapper.toAdminDTO(testOrganization)).thenReturn(testAdminDTO);
        List<AdminDTO> result = service.listAllOrganizations();
        Assertions.assertEquals(List.of(testAdminDTO), result);
        Mockito.verify(organizationRepository).findAll();
        Mockito.verify(mapper).toAdminDTO(testOrganization);
    }

    @Test
    void listAllOrganizations_emptyList() {
        Mockito.when(organizationRepository.findAll()).thenReturn(List.of());
        List<AdminDTO> result = service.listAllOrganizations();
        Assertions.assertEquals(List.of(), result);
        Mockito.verify(organizationRepository).findAll();
        Mockito.verify(mapper, Mockito.never()).toAdminDTO(testUser);
    }

    @Test
    void listAllIndividuals() {
        Individual testIndividual = new Individual(testUser);
        Mockito.when(individualRepository.findAll()).thenReturn(List.of(testIndividual));
        Mockito.when(mapper.toAdminDTO(testIndividual)).thenReturn(testAdminDTO);
        List<AdminDTO> result = service.listAllIndividuals();
        Assertions.assertEquals(List.of(testAdminDTO), result);
        Mockito.verify(individualRepository).findAll();
        Mockito.verify(mapper).toAdminDTO(testIndividual);
    }

    @Test
    void listAllIndividuals_emptyList() {
        Mockito.when(individualRepository.findAll()).thenReturn(List.of());
        List<AdminDTO> result = service.listAllIndividuals();
        Assertions.assertEquals(List.of(), result);
        Mockito.verify(individualRepository).findAll();
        Mockito.verify(mapper, Mockito.never()).toAdminDTO(testUser);
    }

    @Test
    void getAccountDataByUsername_Volunteer() {
        String username = "test";
        Mockito.when(volunteerRepository.findOneByUsername(username)).thenReturn(Optional.of(new Volunteer(testUser)));
        Mockito.when(organizationRepository.findOneByUsername(username)).thenReturn(Optional.empty());
        Mockito.when(individualRepository.findOneByUsername(username)).thenReturn(Optional.empty());
        Object result = service.getAccountDataByUsername(username);
        Assertions.assertEquals(new Volunteer(testUser), result);
        Mockito.verify(volunteerRepository).findOneByUsername(username);
        Mockito.verify(organizationRepository).findOneByUsername(username);
        Mockito.verify(individualRepository).findOneByUsername(username);
    }

    @Test
    void getAccountDataByUsername_Client() {
        String username = "test";
        Mockito.when(volunteerRepository.findOneByUsername(username)).thenReturn(Optional.empty());
        Mockito.when(organizationRepository.findOneByUsername(username)).thenReturn(Optional.of(new Organization(testUser)));
        Mockito.when(individualRepository.findOneByUsername(username)).thenReturn(Optional.empty());
        Object result = service.getAccountDataByUsername(username);
        Assertions.assertEquals(new Organization(testUser), result);
        Mockito.verify(volunteerRepository).findOneByUsername(username);
        Mockito.verify(organizationRepository).findOneByUsername(username);
        Mockito.verify(individualRepository).findOneByUsername(username);
    }

    @Test
    void getAccountDataByUsername_noMatch() {
        String username = "test";
        Mockito.when(volunteerRepository.findOneByUsername(username)).thenReturn(Optional.empty());
        Mockito.when(organizationRepository.findOneByUsername(username)).thenReturn(Optional.empty());
        Mockito.when(individualRepository.findOneByUsername(username)).thenReturn(Optional.empty());
        Object result = service.getAccountDataByUsername(username);
        Assertions.assertNull(result);
        Mockito.verify(volunteerRepository).findOneByUsername(username);
        Mockito.verify(organizationRepository).findOneByUsername(username);
        Mockito.verify(individualRepository).findOneByUsername(username);
    }

}