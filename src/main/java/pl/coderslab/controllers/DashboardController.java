package pl.coderslab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pl.coderslab.dto.ActivitiesDto;
import pl.coderslab.entities.Activity;
import pl.coderslab.entities.BauReport;
import pl.coderslab.entities.Task;
import pl.coderslab.entities.User;
import pl.coderslab.repositories.ActivityRepository;
import pl.coderslab.repositories.BauReportRepository;
import pl.coderslab.repositories.TaskRepository;
import pl.coderslab.repositories.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@Controller
public class DashboardController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BauReportRepository bauReportRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    ActivityRepository activityRepository;


    @GetMapping("/app/activities/getData")
    public ActivitiesDto getAllActivities(HttpServletRequest request, Model model) {
//    public String getAllActivities(HttpServletRequest request, Model model) {

//        List<User> users = taskRepository.findAllAssignedTasksUsers();
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
                try {
                    Activity workingHours = activityRepository.findWorkingHours(users.get(i)).get(0);
                    Activity activeOne = activityRepository.findActiveOne(users.get(i));
                    if (activeOne == null) {
                        List<Activity> userActivities = activityRepository.findActivitiesByUser(users.get(i));
                        Activity lastActivity = userActivities.get(userActivities.size() - 1);
                        activitiesDto.getTimesMatrix()[i][1] = Duration.between(workingHours.getStartTime(), lastActivity.getEndTime()).getSeconds() / 60;
                    } else {
                        activitiesDto.getTimesMatrix()[i][1] = Duration.between(workingHours.getStartTime(), LocalTime.now()).getSeconds() / 60;
                    }
                } catch (Exception e) {
                    //working hours = 0;
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

//        int counter = 0;
//        for (int i = 0; i < activitiesDto.getAssignedTasks().size(); i++) {
//            if (counter == activitiesDto.getUsers().size()) break;
//            Task task = activitiesDto.getAssignedTasks().get(i);
//            Integer userPosition = activitiesDto.getUsers().get(task.getUser().getId());
//            String taskRealDur = (String) activitiesDto.getTimesMatrix()[userPosition][i * 2 +2];
//            String taskEstDur = (String) activitiesDto.getTimesMatrix()[userPosition][i * 2 +3];
//            if (task.getDescription() != null && task.getDescription().equals("Active")) {
//                for (int k = 0; k < activitiesDto.getUsers().size(); k ++) {
//                    for (int j = i; j < activitiesDto.getAssignedTasks().size() - 1; j++ ) {
//                        activitiesDto.getAssignedTasks().set(j, activitiesDto.getAssignedTasks().get(j + 1));
//                        activitiesDto.getTimesMatrix()[k][i * 2 +2] = activitiesDto.getTimesMatrix()[k][i * 2 + 4];
//                        activitiesDto.getTimesMatrix()[k][i * 2 +3] = activitiesDto.getTimesMatrix()[k][i * 2 + 5];
//                    }
//                    activitiesDto.getAssignedTasks().set(activitiesDto.getAssignedTasks().size() -1, task);
//                    activitiesDto.getTimesMatrix()[k][activitiesDto.getTimesMatrix()[k].length - 2] = taskRealDur;
//                    activitiesDto.getTimesMatrix()[k][activitiesDto.getTimesMatrix()[k].length - 1] = taskEstDur;
//                }
//                i--;
//                counter++;
//            }
//        }

        }



        model.addAttribute("activities", objActivities);
        model.addAttribute("activitiesDto", activitiesDto);

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
//        return "app/mainDashboard";
    }

    // ZMIANY

}
