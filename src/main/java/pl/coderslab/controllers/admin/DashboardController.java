package pl.coderslab.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.coderslab.dto.ActivitiesDto;
import pl.coderslab.entities.Activity;
import pl.coderslab.entities.Task;
import pl.coderslab.entities.User;
import pl.coderslab.repositories.ActivityRepository;
import pl.coderslab.repositories.TaskRepository;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    ActivityRepository activityRepository;

    @GetMapping("/admin/dashboard")
    public String getAllActivities(HttpServletRequest request, Model model) {
        return "admin/displayDashboard";
    }

    @GetMapping("/app/activities/getData")
    @ResponseBody
    public ActivitiesDto getData(HttpServletRequest request, Model model) {

        List<User> users = activityRepository.findAllActivitiesUsers();
        ActivitiesDto activitiesDto = new ActivitiesDto();
        List<Object[]> objActivities = new ArrayList<>();

        if (users.size() > 0) {

            for (int i = 0; i < users.size(); i++) {
                Task inactive = new Task();
                inactive.setName("Inactive" + users.get(i).getId());
                inactive.setEstimatedDuration(2700);
                inactive.setUser(users.get(i));
                activitiesDto.getAssignedTasks().add(inactive);
            }

            activitiesDto.getAssignedTasks().addAll(taskRepository.findAllActiveAssignedTasks());
            activitiesDto.getAssignedTasks().addAll(taskRepository.findAllTasksCompletedToday());
            activitiesDto.setTimesMatrix(new Object[ users.size() ][ activitiesDto.getAssignedTasks().size() * 2 + 2 ]);

            for (int i = 0; i < users.size(); i++) {
                for (int j = 0; j < activitiesDto.getTimesMatrix()[i].length ; j++) {
                    activitiesDto.getTimesMatrix()[i][j] = 0;
                }
            }

            for (int i = 0; i < users.size(); i++) {
                activitiesDto.getUsers().put(users.get(i).getId(), i);
                activitiesDto.getTimesMatrix()[i][0] = users.get(i).getFirstName() + " " + users.get(i).getLastName();

                Activity workingHours = activityRepository.findWorkingHours(users.get(i));
                if (workingHours != null) {
                    Activity activeOne = activityRepository.findActiveOne(users.get(i));
                    if (activeOne == null) {
                        List<Activity> userActivities = activityRepository.findActivitiesByUser(users.get(i));
                        Activity lastActivity = userActivities.get(userActivities.size() - 1);
                        activitiesDto.getTimesMatrix()[i][1] = Duration.between(workingHours.getStartTime(), lastActivity.getEndTime()).getSeconds() / 60;
                    } else {
                        activitiesDto.getTimesMatrix()[i][1] = Duration.between(workingHours.getStartTime(), LocalTime.now()).getSeconds() / 60;
                    }
                }

            }
            for (int i = 0; i < activitiesDto.getAssignedTasks().size(); i++) {
                activitiesDto.getActivities().put(activitiesDto.getAssignedTasks().get(i).getName(), i);
            }


            for (Task task : activitiesDto.getAssignedTasks()) {
                User user = task.getUser();
                String activityName = task.getName();
                Integer row = activitiesDto.getUsers().get(user.getId());
                Integer col = activitiesDto.getActivities().get(activityName);
                activitiesDto.getTimesMatrix()[row][col * 2 + 3] = task.getEstimatedDuration() / 60;
                if (task.getName().contains("Inactive")) {
                    task.setName("Bezczynność");
                }
            }


            objActivities = activityRepository.findAllActivities();
            List<Activity> currentlyActive = activityRepository.findAllActive();
            for (Activity activity : currentlyActive) {
                activity.setDuration((Duration.between(activity.getStartTime(), LocalTime.now())).getSeconds());
                for (Object[] object : objActivities) {
                    User objUser = (User) object[0];
                    String objActivityName = (String) object[1];
                    Long objDuration = (Long) object[2];
                    if (activity.getUser().getId() == objUser.getId() && activity.getName().equals(objActivityName)) {
                        object[2] = objDuration + activity.getDuration();
                        object[1] = "[Active]" + (String) object[1];
                    }
                }

            }

            for (Object[] object : objActivities) {
                User user = (User) object[0];
                String activityName = (String) object[1];
                String lookupName = activityName;
                if (activityName.startsWith("[Active]")) {
                    lookupName = activityName.substring(8);
                }
                Integer row = activitiesDto.getUsers().get(user.getId());
                Integer col = 0;
                if (activityName.endsWith("Inactive")) {
                    col = activitiesDto.getActivities().get(lookupName + user.getId());
                } else {
                    col = activitiesDto.getActivities().get(lookupName);
                }
                activitiesDto.getTimesMatrix()[row][col * 2 + 2] = (long) Math.floor((long) object[2] / 60);

                Task task = activitiesDto.getAssignedTasks().get(col);
                task.setDuration(task.getDuration() + (long) object[2]);
                if (activityName.startsWith("[Active]")) {
                    if (activityName.endsWith("Inactive")) {
                        Task newtask = new Task();
                        newtask.setDescription("Active");
                        object[3] = newtask;
                    }
                    task.setDescription("Active");
                    object[1] = lookupName;
                    Task objectTask = (Task) object[3];
                    objectTask.setDescription("Active");
                    object[3] = objectTask;
                }

                object[2] = (long) object[2] / 60;
                if (activityName.endsWith("Inactive")) {
                    Task estDuration = (Task) object[3];
                    if (estDuration == null) {
                        estDuration = new Task();
                    }
                    estDuration.setEstimatedDuration(45);
                    object[3] = estDuration;
                } else {
                    Task estDuration = (Task) object[3];
                    estDuration.setEstimatedDuration(estDuration.getEstimatedDuration() / 60);
                    object[3] = estDuration;
                }

            }

        }

//        model.addAttribute("activities", objActivities);
//        model.addAttribute("activitiesDto", activitiesDto);

        activitiesDto.setObjActivities(objActivities);

        for (Task task : activitiesDto.getAssignedTasks()) {
            task.setActivities(new ArrayList<>());
            task.getUser().setClients(new ArrayList<>());
            task.getUser().setActivities(new ArrayList<>());
            if (task.getClient() != null) {
                task.getClient().setUsers(new ArrayList<>());
                task.getClient().setBauReportList(new ArrayList<>());
            }
        }

        for (Object[] object : activitiesDto.getObjActivities()) {
            User user = (User) object[0];
            user.setClients(new ArrayList<>());
            user.setActivities(new ArrayList<>());
            object[0] = user;
            if (object[3] != null) {
                Task task = (Task) object[3];
                task.setActivities(new ArrayList<>());
                if (task.getUser() != null) {
                    task.getUser().setActivities(new ArrayList<>());
                }
                if (task.getClient() != null) {
                    task.getClient().setUsers(new ArrayList<>());
                    task.getClient().setBauReportList(new ArrayList<>());
                }
                object[3] = task;
            }
        }
        return activitiesDto;
    }

}
