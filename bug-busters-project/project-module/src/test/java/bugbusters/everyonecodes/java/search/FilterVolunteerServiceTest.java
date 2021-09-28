package bugbusters.everyonecodes.java.search;

import bugbusters.everyonecodes.java.usermanagement.data.Volunteer;
import bugbusters.everyonecodes.java.usermanagement.service.UserDTOMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class FilterVolunteerServiceTest {

    @Autowired
    FilterVolunteerService filterVolunteerService;

    @MockBean
    UserDTOMapper userDTOMapper;

    @ParameterizedTest
    @MethodSource("inputParams_filterSearchResults")
    void filterSearchResults(List<Volunteer> searchResults, FilterVolunteer filterVolunteer, List<Volunteer> expected) {
        List<Volunteer> result = filterVolunteerService.filterSearchResults(searchResults, filterVolunteer);
        Assertions.assertEquals(expected, result);
    }

    private static Stream<Arguments> inputParams_filterSearchResults() {
        Volunteer volunteer1 = new Volunteer();
        volunteer1.setSkills(Set.of("a", "b", "c"));
        volunteer1.setUsername("user1");
        volunteer1.setRatings(List.of(5));

        Volunteer volunteer2 = new Volunteer();
        volunteer2.setSkills(Set.of("a", "b", "d"));
        volunteer2.setUsername("user2");
        volunteer2.setRatings(List.of(2));

        Volunteer volunteer3 = new Volunteer();
        volunteer3.setSkills(Set.of("g"));
        volunteer3.setUsername("user3");
        volunteer3.setRatings(List.of(3));

        Volunteer volunteer4 = new Volunteer();
        volunteer4.setSkills(Set.of("a", "c", "d", "f"));
        volunteer4.setUsername("user4");
        volunteer4.setRatings(List.of(4));

        List<Volunteer> input = List.of(volunteer1, volunteer2, volunteer3, volunteer4);

        return Stream.of(
                //no filter returns input
                Arguments.of(
                        input, new FilterVolunteer(null, null),
                        input
                ),
                //filter by skills
                Arguments.of(
                        input, new FilterVolunteer("a", null),
                        List.of(volunteer1, volunteer2, volunteer4)
                ),
                //filter by rating
                Arguments.of(
                        input, new FilterVolunteer(null, 3),
                        List.of(volunteer1, volunteer3, volunteer4)
                ),
                //filter by both
                Arguments.of(
                        input, new FilterVolunteer("d", 3),
                        List.of(volunteer4)
                ),
                //filter out everything
                Arguments.of(
                        input, new FilterVolunteer("x", 5),
                        List.of()
                )
        );
    }

}