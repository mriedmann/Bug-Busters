package bugbusters.everyonecodes.java.usermanagement.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VolunteerPrivateDTO {
    @NotEmpty
    private String username;
    @Pattern(regexp = "^[a-zA-Z\s;]*$")
    private String skills;
}
