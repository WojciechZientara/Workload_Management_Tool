package pl.coderslab.controllers.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
        response.sendRedirect(request.getContextPath() + "/app/userPanel");
        return null;
    }

    @GetMapping("/app/console/workEnd")
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

        response.sendRedirect(request.getContextPath() + "/app/userPanel");
        return null;
    }

    @PostMapping("/app/console/activateTask")
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

        response.sendRedirect(request.getContextPath() + "/app/userPanel");
        return null;
    }


    @GetMapping("/app/console/stopTask")
    public String getStop(Model model,
                          HttpServletRequest request, HttpServletResponse response ) throws Exception {
        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long) session.getAttribute("id"));

        try {
            Activity workingHours = activityRepository.findWorkingHours(user).get(0);
            if (workingHours.getEndTime() == null) {
                Activity activity = activityRepository.findActiveOne(user);
                if (activity != null && !activity.getName().endsWith("Inactive")) {
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

        response.sendRedirect(request.getContextPath() + "/app/userPanel");
        return null;
    }

    @GetMapping("/app/console/finishTask")
    public String getFinish(Model model,
                            HttpServletRequest request, HttpServletResponse response ) throws Exception {

        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long) session.getAttribute("id"));

        try {
            Activity workingHours = activityRepository.findWorkingHours(user).get(0);
            if (workingHours.getEndTime() == null) {
                Activity activity = activityRepository.findActiveOne(user);
                if (activity != null && !activity.getName().endsWith("Inactive")) {
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

        response.sendRedirect(request.getContextPath() + "/app/userPanel");
        return null;
    }
}
