package bugbusters.everyonecodes.java.usermanagement.rolemanagement;

import bugbusters.everyonecodes.java.usermanagement.UserFactory;
import bugbusters.everyonecodes.java.usermanagement.data.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserFactoryTest {

    @Autowired
    UserFactory userFactory;

    @Test
    void createRole_Volunteer() {
        UserDTO userDTO = new UserDTO(null, "", "ROLE_VOLUNTEER", null, null, null, null, null);
        User user = userFactory.createUser(userDTO);
        Assertions.assertTrue(user instanceof Volunteer);
    }

    @Test
    void createRole_Organization() {
        UserDTO userDTO = new UserDTO(null, "", "ROLE_ORGANIZATION", null, null, null, null, null);
        User user = userFactory.createUser(userDTO);
        Assertions.assertTrue(user instanceof Organization);
    }

    @Test
    void createRole_Individual() {
        UserDTO userDTO = new UserDTO(null, "", "ROLE_INDIVIDUAL", null, null, null, null, null);
        User user = userFactory.createUser(userDTO);
        Assertions.assertTrue(user instanceof Individual);
    }

    @Test
    void createRole_invalidInput() {
        UserDTO userDTO = new UserDTO(null, "", "", null, null, null, null, null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> userFactory.createUser(userDTO));
    }
}