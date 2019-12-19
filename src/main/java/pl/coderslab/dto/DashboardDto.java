package pl.coderslab.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.coderslab.entities.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter @Getter
@NoArgsConstructor
public class DashboardDto {

    private List<Task> assignedTasks = new ArrayList<>();
    private Map<Long, Integer> users = new HashMap<>();
    private Map<String, Integer> activities = new HashMap<>();
    private Object[][] timesMatrix;
    private List<Object[]> allActivities;

}
