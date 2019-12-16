package pl.coderslab.controllers.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import pl.coderslab.entities.*;
import pl.coderslab.repositories.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
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
    public String getWorkStart(HttpServletRequest request, HttpServletResponse response ) throws Exception {
        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long) session.getAttribute("id"));
        if (activityRepository.findWorkingHours(user) == null) {
            Activity workingHours = new Activity();
            workingHours.setDate(LocalDate.now());
            workingHours.setStartTime(LocalTime.now());
            workingHours.setUser(user);
            workingHours.setName("Working Hours");
            activityRepository.save(workingHours);
            setInactive(user);
        }
        response.sendRedirect(request.getContextPath() + "/app/userPanel");
        return null;
    }

    @GetMapping("/app/console/workEnd")
    public String getWorkEnd(HttpServletRequest request, HttpServletResponse response ) throws Exception {
        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long) session.getAttribute("id"));

        Activity workingHours = activityRepository.findWorkingHours(user);
        if (workingHours != null && workingHours.getEndTime() == null) {
            workingHours.setEndTime(LocalTime.now());
            workingHours.setDuration((Duration.between(workingHours.getStartTime(), workingHours.getEndTime())).getSeconds());
            activityRepository.save(workingHours);
        }

        //close active task
        Activity activity = activityRepository.findActiveOne(user);
        if (activity != null) {
            activity.setEndTime(LocalTime.now());
            activity.setDuration((Duration.between(activity.getStartTime(), activity.getEndTime())).getSeconds());
            activityRepository.save(activity);
        }

        response.sendRedirect(request.getContextPath() + "/app/userPanel");
        return null;
    }

    @PostMapping("/app/console/activateTask")
    public String postActivateTask(@Valid Activity activity, BindingResult result,
                                   HttpServletRequest request, HttpServletResponse response ) throws Exception {
        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long)session.getAttribute("id"));

        Activity workingHours = activityRepository.findWorkingHours(user);
        Activity activeOne = activityRepository.findActiveOne(user);

        if (workingHours != null && workingHours.getEndTime() == null && /* user is working */
            activity.getTask() != null && /* selected activity is not 'Inactive' */
            !activeOne.getName().equals(activity.getTask().getName())) { /* selected activity is not active task */

            if (activeOne != null) {
                activeOne.setEndTime(LocalTime.now());
                activeOne.setDuration((Duration.between(activeOne.getStartTime(), activeOne.getEndTime())).getSeconds());
                activityRepository.save(activeOne);
            }
            activity.setName(activity.getTask().getName());
            activity.setUser(user);
            activity.setDate(LocalDate.now());
            activity.setStartTime(LocalTime.now());
            activityRepository.save(activity);
        }
        response.sendRedirect(request.getContextPath() + "/app/userPanel");
        return null;
    }


    @GetMapping("/app/console/stopTask")
    public String getStop(HttpServletRequest request, HttpServletResponse response ) throws Exception {
        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long) session.getAttribute("id"));

        finishCurrentActivity(user);
        response.sendRedirect(request.getContextPath() + "/app/userPanel");
        return null;
    }

    @GetMapping("/app/console/finishTask")
    public String getFinish(HttpServletRequest request, HttpServletResponse response ) throws Exception {

        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long) session.getAttribute("id"));

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
                }
            }
        }
        response.sendRedirect(request.getContextPath() + "/app/userPanel");
        return null;
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
