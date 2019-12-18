package pl.coderslab.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@NoArgsConstructor
public class TaskOperationsDto {

    private String type;
    private String taskName;
    private long taskId;

}
