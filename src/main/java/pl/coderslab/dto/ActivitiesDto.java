package pl.coderslab.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.coderslab.entities.Activity;
import pl.coderslab.entities.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter @Getter
@NoArgsConstructor
public class ActivitiesDto {

    private Map<String, Integer> activities = new HashMap<>();
    private Map<Long, Integer> users = new HashMap<>();
    private Object[][] timesMatrix;

}
