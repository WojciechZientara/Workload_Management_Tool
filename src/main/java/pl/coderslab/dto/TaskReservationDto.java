package pl.coderslab.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.coderslab.entities.Task;

import java.util.List;

@Setter @Getter
@NoArgsConstructor
public class TaskReservationDto {

    private String type;
    private long taskId;
    private String userName;
    private Object[][] dropdownTasks;

}
