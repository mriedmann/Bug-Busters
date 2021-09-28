package bugbusters.everyonecodes.java.usermanagement.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDTO {
    private String username;
    private Double rating;
    private int activitiesPending;
    private int activitiesInProgress;
    private int activitiesCompleted;
}
