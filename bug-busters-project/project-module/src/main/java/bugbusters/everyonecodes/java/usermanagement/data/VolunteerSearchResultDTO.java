package bugbusters.everyonecodes.java.usermanagement.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VolunteerSearchResultDTO {
    private String username;
    private String skills;
    private Double rating;
}
