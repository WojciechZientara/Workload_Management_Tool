package pl.coderslab.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@NoArgsConstructor
public class WorkTimeDto {

    private String type;
    private String startTime;
    private String endTime;

}
