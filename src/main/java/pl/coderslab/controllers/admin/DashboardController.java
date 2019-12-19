package pl.coderslab.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.coderslab.dto.DashboardDto;
import pl.coderslab.entities.Activity;
import pl.coderslab.entities.Task;
import pl.coderslab.entities.User;
import pl.coderslab.repositories.ActivityRepository;
import pl.coderslab.repositories.TaskRepository;

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
    public String getAllActivities() {
        return "admin/displayDashboard";
    }

    @GetMapping("/app/activities/getData")
    @ResponseBody
    public DashboardDto getData() {

        DashboardDto dto = new DashboardDto();
        List<User> users = activityRepository.findAllActivitiesUsers();

        if (users.size() > 0) {
            setDtoAssignedTasksList(dto, users);
            setDtoUsersAndTasksMapsForTimesMatrixRowsAndCols(dto, users);
            initializeDtoTimesMatrix(dto, users);
            populateMatrixWithEstimatedTasksDurations(dto);
            setAllActivitiesCurrentDurationsAndMarkActiveOnes(dto);
            populateMatrixWithCurrentTasksDurationsAndUpdateTasks(dto);
            prepareDtoForJson(dto);
        }
        return dto;
    }

    public void setDtoAssignedTasksList(DashboardDto dto, List<User> users) {
        addInactivityTaskToDtoForEachActiveUser(dto, users);
        addActiveAndCompletedTasksAssignedTodayToDto(dto);
    }

    public void addInactivityTaskToDtoForEachActiveUser(DashboardDto dto, List<User> users){
        for (int i = 0; i < users.size(); i++) {
            Task inactive = new Task();
            User currentUser = users.get(i);
            inactive.setUser(currentUser);
            inactive.setName("Inactive" + currentUser.getId());
            inactive.setEstimatedDuration(2700);
            dto.getAssignedTasks().add(inactive);
        }
    }

    public void addActiveAndCompletedTasksAssignedTodayToDto (DashboardDto dto) {
        dto.getAssignedTasks().addAll(taskRepository.findAllActiveAssignedTasks());
        dto.getAssignedTasks().addAll(taskRepository.findAllTasksCompletedToday());
    }

    public void setDtoUsersAndTasksMapsForTimesMatrixRowsAndCols(DashboardDto dto, List<User> users) {
        for (int i = 0; i < users.size(); i++) {
            User currentUser = users.get(i);
            dto.getUsers().put(currentUser.getId(), i);
        }
        for (int i = 0; i < dto.getAssignedTasks().size(); i++) {
            Task currentTask = dto.getAssignedTasks().get(i);
            dto.getActivities().put(currentTask.getName(), i);
        }
    }

    public void initializeDtoTimesMatrix(DashboardDto dto, List<User> users) {
        int rows = users.size();
        int cols = dto.getAssignedTasks().size() * 2 + 2;
        dto.setTimesMatrix(new Object[rows][cols]);

        for (int i = 0; i < rows; i++) {
            User currentUser = users.get(i);
            String currentUserName = currentUser.getFirstName() + " " + currentUser.getLastName();
            dto.getTimesMatrix()[i][0] = currentUserName;

            Long currentUserWorkTime = getUsersCurrentWorkTime(currentUser);
            dto.getTimesMatrix()[i][1] = currentUserWorkTime;

            for (int j = 2; j < cols; j++) {
                dto.getTimesMatrix()[i][j] = 0;
            }
        }
    }

    public Long getUsersCurrentWorkTime(User user) {
            Long currentWorkTime = null;
            Activity workingHours = activityRepository.findWorkingHours(user);
            if (workingHours != null) {
                Activity activeOne = activityRepository.findActiveOne(user);
                if (activeOne == null) {
                    List<Activity> userActivities = activityRepository.findActivitiesByUser(user);
                    Activity lastActivity = userActivities.get(userActivities.size() - 1);
                    currentWorkTime = Duration.between(workingHours.getStartTime(), lastActivity.getEndTime()).getSeconds() / 60;
                } else {
                    currentWorkTime = Duration.between(workingHours.getStartTime(), LocalTime.now()).getSeconds() / 60;
                }
            }
            return currentWorkTime;
    }

    public void populateMatrixWithEstimatedTasksDurations(DashboardDto dto) {
        for (Task task : dto.getAssignedTasks()) {
            User user = task.getUser();
            String activityName = task.getName();
            int row = dto.getUsers().get(user.getId());
            int col = dto.getActivities().get(activityName);

            dto.getTimesMatrix()[row][col * 2 + 3] = task.getEstimatedDuration() / 60;
            if (task.getName().contains("Inactive")) {
                task.setName("Bezczynność");
            }
        }
    }

    public void setAllActivitiesCurrentDurationsAndMarkActiveOnes(DashboardDto dto) {
        dto.setAllActivities(activityRepository.findAllActivities());

        List<Activity> currentlyActive = activityRepository.findAllActive();
        for (Activity activity : currentlyActive) {
            activity.setDuration((Duration.between(activity.getStartTime(), LocalTime.now())).getSeconds());
            for (Object[] object : dto.getAllActivities()) {
                User objUser = (User) object[0];
                String objActivityName = (String) object[1];
                Long objDuration = (Long) object[2];
                if (activity.getUser().getId() == objUser.getId() && activity.getName().equals(objActivityName)) {
                    object[2] = objDuration + activity.getDuration();
                    object[1] = "[Active]" + (String) object[1];
                }
            }
        }
    }

    public void populateMatrixWithCurrentTasksDurationsAndUpdateTasks(DashboardDto dto) {
        for (Object[] object : dto.getAllActivities()) {
            User user = (User) object[0];
            String activityName = (String) object[1];

            int row = dto.getUsers().get(user.getId());
            int col = getCol(dto, user, activityName);

            dto.getTimesMatrix()[row][col * 2 + 2] = (long) Math.floor((long) object[2] / 60);
            updateTask(dto, object, col, activityName);

        }
    }

    public String getLookupName(String activityName) {
        String lookupName = activityName;
        if (activityName.startsWith("[Active]")) {
            lookupName = activityName.substring(8);
        }
        return lookupName;
    }

    public Integer getCol(DashboardDto dto, User user, String activityName) {
        Integer col = 0;
        String lookupName = getLookupName(activityName);
        if (activityName.endsWith("Inactive")) {
            col = dto.getActivities().get(lookupName + user.getId());
        } else {
            col = dto.getActivities().get(lookupName);
        }
        return col;
    }

        public void updateTask(DashboardDto dto, Object[] object, int col, String activityName) {
            String lookupName = getLookupName(activityName);

            Task task = dto.getAssignedTasks().get(col);
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

        public void prepareDtoForJson(DashboardDto dto) {
            for (Task task : dto.getAssignedTasks()) {
                task.setActivities(new ArrayList<>());
                task.getUser().setClients(new ArrayList<>());
                task.getUser().setActivities(new ArrayList<>());
                if (task.getClient() != null) {
                    task.getClient().setUsers(new ArrayList<>());
                    task.getClient().setBauReportList(new ArrayList<>());
                }
            }

            for (Object[] object : dto.getAllActivities()) {
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
        }

}
