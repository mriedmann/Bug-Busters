package bugbusters.everyonecodes.java.usermanagement.endpoints;

import bugbusters.everyonecodes.java.usermanagement.api.UserDTO;
import bugbusters.everyonecodes.java.usermanagement.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserEndpointTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @MockBean
    UserService userService;

    private final String url = "/users";


    //registerUser Test

    @Test
    void registerUser_invalidPassword() {
        UserDTO testUser = new UserDTO ("Test",
                "invalidPassword",
                "ROLE_TEST",
                "Test Test",
                null,
                null,
                "test.test@bugbusters.com",
                null);
        Mockito.when(userService.saveUser(testUser)).thenThrow(IllegalArgumentException.class);
        ResponseEntity<UserDTO> resultResponseEntity = testRestTemplate.postForEntity(url + "/register", testUser, UserDTO.class);
        HttpStatus statusCode = resultResponseEntity.getStatusCode();
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, statusCode);
        Mockito.verify(userService, Mockito.times(1)).saveUser(Mockito.any(UserDTO.class));
    }

    @Test
    void registerUser_validationFailed() {
        UserDTO testUser = new UserDTO ("Test",
                "validPassword1#",
                null,
                null,
                null,
                null,
                null,
                null);
        ResponseEntity<UserDTO> resultResponseEntity = testRestTemplate.postForEntity(url + "/register", testUser, UserDTO.class);
        HttpStatus statusCode = resultResponseEntity.getStatusCode();
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, statusCode);
        Mockito.verify(userService, Mockito.never()).saveUser(Mockito.any(UserDTO.class));
    }

    @Test
    void registerUser_invalidEmail() {
        UserDTO testUser = new UserDTO ("Test",
                "validPassword1#",
                "ROLE_TEST",
                "Test Test",
                null,
                null,
                "totallyValidEmail",
                null);
        ResponseEntity<UserDTO> resultResponseEntity = testRestTemplate.postForEntity(url + "/register", testUser, UserDTO.class);
        HttpStatus statusCode = resultResponseEntity.getStatusCode();
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, statusCode);
        Mockito.verify(userService, Mockito.never()).saveUser(Mockito.any(UserDTO.class));
    }

    @Test
    void registerUser_validNewUser() {
        UserDTO testUser = new UserDTO ("Test",
                "validPassword1#",
                "ROLE_TEST",
                "Test Test",
                null,
                null,
                "test.test@bugbusters.com",
                null);
        Mockito.when(userService.saveUser(testUser)).thenReturn(testUser);
        ResponseEntity<UserDTO> resultResponseEntity = testRestTemplate.postForEntity(url + "/register", testUser, UserDTO.class);
        UserDTO result = resultResponseEntity.getBody();
        HttpStatus resultStatusCode = resultResponseEntity.getStatusCode();
        Assertions.assertEquals(testUser, result);
        Assertions.assertEquals(HttpStatus.OK, resultStatusCode);
        Mockito.verify(userService).saveUser(testUser);
    }

}