package pl.coderslab.controllers.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.dto.TaskOperationsDto;
import pl.coderslab.dto.WorkTimeDto;
import pl.coderslab.entities.*;
import pl.coderslab.repositories.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RestController
public class UserConsoleController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    BauReportRepository bauReportRepository;

    @GetMapping("/app/console/workStart")
    public WorkTimeDto getWorkStart(HttpServletRequest request, HttpServletResponse response ) throws Exception {
        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long) session.getAttribute("id"));
        WorkTimeDto workTimeDto = new WorkTimeDto();
        if (activityRepository.findWorkingHours(user) == null) {
            Activity workingHours = new Activity();
            workingHours.setDate(LocalDate.now());
            workingHours.setStartTime(LocalTime.now());
            workingHours.setUser(user);
            workingHours.setName("Working Hours");
            activityRepository.save(workingHours);
            setInactive(user);

            workTimeDto.setType("startWork");
            workTimeDto.setStartTime(workingHours.getStartTime().format(DateTimeFormatter.ofPattern("H:mm:ss")));
        }
        return workTimeDto;
    }

    @GetMapping("/app/console/workEnd")
    public WorkTimeDto getWorkEnd(HttpServletRequest request, HttpServletResponse response ) throws Exception {
        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long) session.getAttribute("id"));
        WorkTimeDto workTimeDto = new WorkTimeDto();

        Activity workingHours = activityRepository.findWorkingHours(user);
        if (workingHours != null && workingHours.getEndTime() == null) {
            workingHours.setEndTime(LocalTime.now());
            workingHours.setDuration((Duration.between(workingHours.getStartTime(), workingHours.getEndTime())).getSeconds());
            activityRepository.save(workingHours);

            workTimeDto.setType("endWork");
            workTimeDto.setEndTime(workingHours.getEndTime().format(DateTimeFormatter.ofPattern("H:mm:ss")));
        }

        //close active task
        Activity activity = activityRepository.findActiveOne(user);
        if (activity != null) {
            activity.setEndTime(LocalTime.now());
            activity.setDuration((Duration.between(activity.getStartTime(), activity.getEndTime())).getSeconds());
            activityRepository.save(activity);
        }

        return workTimeDto;
    }

    @GetMapping("/app/console/activateTask/{taskId}")
    public TaskOperationsDto postActivateTask(@PathVariable long taskId,
                          HttpServletRequest request, HttpServletResponse response ) throws Exception {
        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long)session.getAttribute("id"));
        Task selectedTask = taskRepository.findOne(taskId);

        Activity workingHours = activityRepository.findWorkingHours(user);
        Activity activeOne = activityRepository.findActiveOne(user);
        TaskOperationsDto taskOperationsDto = new TaskOperationsDto();

        if (workingHours != null && workingHours.getEndTime() == null && /* user is working */
                taskId != 0 && /* selected activity is not 'Inactive' */
                !activeOne.getName().equals(selectedTask.getName())) { /* selected activity is not active task */

            if (activeOne != null) {
                activeOne.setEndTime(LocalTime.now());
                activeOne.setDuration((Duration.between(activeOne.getStartTime(), activeOne.getEndTime())).getSeconds());
                activityRepository.save(activeOne);
            }
            Activity newActivity = new Activity();
            newActivity.setName(selectedTask.getName());
            newActivity.setUser(user);
            newActivity.setDate(LocalDate.now());
            newActivity.setStartTime(LocalTime.now());
            newActivity.setTask(selectedTask);
            activityRepository.save(newActivity);

            taskOperationsDto.setType("activateTask");
            taskOperationsDto.setTaskName(selectedTask.getClient().getName() + " - " + selectedTask.getName());
        }
        return taskOperationsDto;
    }

    @GetMapping("/app/console/stopTask")
    public TaskOperationsDto getStop(HttpServletRequest request, HttpServletResponse response ) throws Exception {
        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long) session.getAttribute("id"));
        finishCurrentActivity(user);

        TaskOperationsDto taskOperationsDto = new TaskOperationsDto();
        taskOperationsDto.setType("stop");
        return taskOperationsDto;
    }

    @GetMapping("/app/console/finishTask")
    public TaskOperationsDto getFinish(HttpServletRequest request, HttpServletResponse response ) throws Exception {

        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long) session.getAttribute("id"));
        TaskOperationsDto taskOperationsDto = new TaskOperationsDto();

        Activity workingHours = activityRepository.findWorkingHours(user);
        if (workingHours != null && workingHours.getEndTime() == null) {
            Activity activity = finishCurrentActivity(user);
            if (activity != null) {
                Task task = activity.getTask();
                if (task != null) {
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

                    taskOperationsDto.setType("finish");
                    taskOperationsDto.setTaskName(task.getName());
                    taskOperationsDto.setTaskId(task.getId());
                }
            }
        }
        return taskOperationsDto;
    }

    void setInactive(User user) {
        Activity idle = new Activity();
        idle.setDate(LocalDate.now());
        idle.setStartTime(LocalTime.now());
        idle.setUser(user);
        idle.setName("Inactive");
        activityRepository.save(idle);
    }

    Activity finishCurrentActivity(User user) {
        Activity activity = null;
        Activity workingHours = activityRepository.findWorkingHours(user);
        if (workingHours != null && workingHours.getEndTime() == null) {
            activity = activityRepository.findActiveOne(user);
            if (activity != null && !activity.getName().endsWith("Inactive")) {
                activity.setEndTime(LocalTime.now());
                activity.setDuration((Duration.between(activity.getStartTime(), activity.getEndTime())).getSeconds());
                activityRepository.save(activity);
                setInactive(user);
            }
        }
        return activity;
    }
}
