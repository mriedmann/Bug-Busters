package bugbusters.everyonecodes.java.usermanagement.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPublicDTO {
    private String username;
    private String fullName;
    private Integer age;
    private String description;
    private Double rating;
    private Integer experience;
}
