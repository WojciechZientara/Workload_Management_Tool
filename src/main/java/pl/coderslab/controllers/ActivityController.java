package pl.coderslab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import pl.coderslab.dto.ActivitiesDto;
import pl.coderslab.entities.*;
import pl.coderslab.repositories.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class ActivityController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BauReportRepository bauReportRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    ActivityRepository activityRepository;

    @GetMapping("/app/activities")
    public String getActivities(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long)session.getAttribute("id"));
        List<Activity> activities = activityRepository.findActivitiesByUser(user);
        model.addAttribute("activities", activities);
        return "app/activities";
    }

    @GetMapping("/app/main/activity/workStart")
    public String getWorkStart(Model model,
                               HttpServletRequest request, HttpServletResponse response ) throws Exception {
        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long) session.getAttribute("id"));
        Activity workingHours = new Activity();
        try {
            workingHours = activityRepository.findWorkingHours(user).get(0);
        } catch (Exception e) {
            if (workingHours.getEndTime() == null) {
                workingHours.setDate(LocalDate.now());
                workingHours.setStartTime(LocalTime.now());
                workingHours.setUser(user);
                workingHours.setName("Working Hours");
                activityRepository.save(workingHours);

                Activity idle = new Activity();
                idle.setDate(LocalDate.now());
                idle.setStartTime(LocalTime.now());
                idle.setUser(user);
                idle.setName("Inactive");
                activityRepository.save(idle);
            }
        }
        response.sendRedirect(request.getContextPath() + "/app/main");
        return null;
    }

    @GetMapping("/app/main/activity/workEnd")
    public String getWorkEnd(Model model,
                               HttpServletRequest request, HttpServletResponse response ) throws Exception {
        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long) session.getAttribute("id"));

        try {
            Activity workingHours = activityRepository.findWorkingHours(user).get(0);
            if (workingHours.getStartTime() == null || workingHours.getEndTime() != null) {
                throw new Exception();
            }
            workingHours.setEndTime(LocalTime.now());
            workingHours.setDuration((Duration.between(workingHours.getStartTime(), workingHours.getEndTime())).getSeconds());
            activityRepository.save(workingHours);

            Activity activity = activityRepository.findActiveOne(user);
            if (activity != null) {
                activity.setEndTime(LocalTime.now());
                activity.setDuration((Duration.between(activity.getStartTime(), activity.getEndTime())).getSeconds());
                activityRepository.save(activity);
            }
        } catch (Exception e) {
            //no start time
        }

        response.sendRedirect(request.getContextPath() + "/app/main");
        return null;
    }

    @GetMapping("/app/main/activity/stop")
    public String getStop(Model model,
                             HttpServletRequest request, HttpServletResponse response ) throws Exception {
        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long) session.getAttribute("id"));

        try {
            Activity workingHours = activityRepository.findWorkingHours(user).get(0);
            if (workingHours.getEndTime() == null) {
                Activity activity = activityRepository.findActiveOne(user);
                if (activity != null && !activity.getName().equals("Inactive")) {
                    activity.setEndTime(LocalTime.now());
                    activity.setDuration((Duration.between(activity.getStartTime(), activity.getEndTime())).getSeconds());
                    activityRepository.save(activity);
                    Activity idle = new Activity();
                    idle.setDate(LocalDate.now());
                    idle.setStartTime(LocalTime.now());
                    idle.setUser(user);
                    idle.setName("Inactive");
                    activityRepository.save(idle);
                }
            }
        } catch (Exception e) {
            //no records yet
        }

        response.sendRedirect(request.getContextPath() + "/app/main");
        return null;
    }

    @GetMapping("/app/main/activity/finish")
    public String getFinish(Model model,
                          HttpServletRequest request, HttpServletResponse response ) throws Exception {

        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long) session.getAttribute("id"));

        try {
            Activity workingHours = activityRepository.findWorkingHours(user).get(0);
            if (workingHours.getEndTime() == null) {
                Activity activity = activityRepository.findActiveOne(user);
                if (activity != null && !activity.getName().equals("Inactive")) {
                    activity.setEndTime(LocalTime.now());
                    activity.setDuration((Duration.between(activity.getStartTime(), activity.getEndTime())).getSeconds());
                    activityRepository.save(activity);

                    Task task = activity.getTask();
                    task.setDuration(activityRepository.findSumOfActivitiesDurationByTask(task));
                    task.setDateCompleted(LocalDate.now());
                    task.setCompleted(true);
                    taskRepository.save(task);
                    if (task.getType().equals("BAU")) {
                        BauReport bauReport = task.getBauArchetype();
                        bauReport.setSumOfDuration(bauReport.getSumOfDuration() + task.getDuration());
                        bauReport.setNumberOfRuns(bauReport.getNumberOfRuns() + 1);
                        bauReport.setAverageDuration(bauReport.getSumOfDuration() / bauReport.getNumberOfRuns());
                        bauReportRepository.save(bauReport);
                    }

                    Activity idle = new Activity();
                    idle.setDate(LocalDate.now());
                    idle.setStartTime(LocalTime.now());
                    idle.setUser(user);
                    idle.setName("Inactive");
                    activityRepository.save(idle);
                }
            }
        } catch (Exception e) {
            //no records yet
        }

        response.sendRedirect(request.getContextPath() + "/app/main");
        return null;
    }

    @PostMapping("/app/main/activate")
    public String postActivateTask(@Valid Activity activity, BindingResult result, Model model,
                               HttpServletRequest request, HttpServletResponse response ) throws Exception {
        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long)session.getAttribute("id"));

        try {

            Activity workingHours = activityRepository.findWorkingHours(user).get(0);
            Activity active = activityRepository.findActiveOne(user);

            if (activity.getTask() == null || workingHours.getEndTime() != null ||
                    active.getName().equals(activity.getTask().getName())) {
                throw new Exception();
            }

            if (active != null) {
                active.setEndTime(LocalTime.now());
                active.setDuration((Duration.between(active.getStartTime(), active.getEndTime())).getSeconds());
                activityRepository.save(active);
            }
            activity.setName(activity.getTask().getName());
            activity.setUser(user);
            activity.setDate(LocalDate.now());
            activity.setStartTime(LocalTime.now());
            activityRepository.save(activity);
        } catch (Exception e) {
            //no start time
        }

        response.sendRedirect(request.getContextPath() + "/app/main");
        return null;
    }

    @GetMapping("/app/activities/all")
    public String getAllActivities(HttpServletRequest request, Model model) {

        List<User> users = taskRepository.findAllAssignedTasksUsers();

        ActivitiesDto activitiesDto = new ActivitiesDto();

        for (int i = 0; i < users.size(); i++) {
            Task inactive = new Task();
            inactive.setName("Inactive" + users.get(i).getId());
            inactive.setEstimatedDuration(45L);
            inactive.setUser(users.get(i));
            activitiesDto.getAssignedTasks().add(inactive);
        }

        activitiesDto.getAssignedTasks().addAll(taskRepository.findAllAssignedTasks());
        activitiesDto.setTimesMatrix(new String[ users.size() ][ activitiesDto.getAssignedTasks().size() * 2 + 2 ]);

        for (int i = 0; i < users.size(); i++) {
            for (int j = 0; j < activitiesDto.getTimesMatrix()[i].length ; j++) {
                    activitiesDto.getTimesMatrix()[i][j] = "0";
            }
        }

        for (int i = 0; i < users.size(); i++) {
            activitiesDto.getUsers().put(users.get(i).getId(), i);
            activitiesDto.getTimesMatrix()[i][0] = users.get(i).getFirstName() + " " + users.get(i).getLastName();
            try {
                Activity workingHours = activityRepository.findWorkingHours(users.get(i)).get(0);
                activitiesDto.getTimesMatrix()[i][1] = String.valueOf(Duration.between(workingHours.getStartTime(), LocalTime.now()).getSeconds() / 60);
            } catch (Exception e) {
                activitiesDto.getTimesMatrix()[i][1] = "0";
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
            activitiesDto.getTimesMatrix()[row][col * 2 + 3] = String.valueOf(task.getEstimatedDuration() / 60);
            if (task.getName().startsWith("Inactive")) {
                task.setName("Bezczynność");
            }
        }


        List<Object[]> objActivities = activityRepository.findAllActivities();
        List<Activity> currentlyActive = activityRepository.findAllActive();
        for (Activity activity : currentlyActive) {
            activity.setDuration((Duration.between(activity.getStartTime(), LocalTime.now())).getSeconds());
            for (Object[] object : objActivities) {
                User objUser = (User) object[0];
                String objActivityName = (String) object[1];
                Long objDuration = (Long) object[2];
                if (activity.getUser().getId() == objUser.getId() && activity.getName().equals(objActivityName)) {
                    object[2] = objDuration + activity.getDuration();
                }
            }
        }



        for (Object[] object : objActivities) {
            User user = (User) object[0];
            String activityName = (String) object[1];
            Integer row = activitiesDto.getUsers().get(user.getId());
            Integer col = 0;
            if (activityName.equals("Inactive")) {
                col = activitiesDto.getActivities().get(activityName + user.getId());
            } else {
                col = activitiesDto.getActivities().get(activityName);
            }
            activitiesDto.getTimesMatrix()[row][col * 2 + 2] = String.valueOf(Math.floor((long) object[2] / 60));

            Long duration = activitiesDto.getAssignedTasks().get(col).getDuration();
            activitiesDto.getAssignedTasks().get(col).setDuration(duration + (long) object[2]);

            object[2] = (long) object[2] / 60;
            if (activityName.equals("Inactive")) {
                Task estDuration = new Task();
                estDuration.setEstimatedDuration(45);
                object[3] = estDuration;
            } else {
                Task estDuration = (Task) object[3];
                estDuration.setEstimatedDuration(estDuration.getEstimatedDuration() / 60);
                object[3] = estDuration;
            }

        }

        model.addAttribute("activities", objActivities);
        model.addAttribute("activitiesDto", activitiesDto);
        return "app/mainDashboard";
    }

}
