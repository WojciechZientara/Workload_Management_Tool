package pl.coderslab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import pl.coderslab.entities.*;
import pl.coderslab.repositories.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ActivityController {

    @Autowired
    UserRepository userRepository;

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
            workingHours.setDate(LocalDate.now());
            workingHours.setStartTime(LocalTime.now());
            workingHours.setUser(user);
            workingHours.setName("Working Hours");
            activityRepository.save(workingHours);
        }
        response.sendRedirect(request.getContextPath() + "/app/main");
        return null;
    }

    @GetMapping("/app/main/activity/workEnd")
    public String getWorkEnd(Model model,
                               HttpServletRequest request, HttpServletResponse response ) throws Exception {
        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long) session.getAttribute("id"));

        Activity activity = activityRepository.findActiveOne(user);
        if (activity != null) {
            activity.setEndTime(LocalTime.now());
            activity.setDuration(Duration.between(activity.getStartTime(), activity.getEndTime()));
            activityRepository.save(activity);
        }

        try {
            Activity workingHours = activityRepository.findWorkingHours(user).get(0);
            workingHours.setEndTime(LocalTime.now());
            workingHours.setDuration(Duration.between(workingHours.getStartTime(), workingHours.getEndTime()));
            activityRepository.save(workingHours);
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

        Activity activity = activityRepository.findActiveOne(user);
        if (activity != null && !activity.getName().equals("Inactive")) {
            activity.setEndTime(LocalTime.now());
            activity.setDuration(Duration.between(activity.getStartTime(), activity.getEndTime()));
            activityRepository.save(activity);
            Activity idle = new Activity();
            idle.setDate(LocalDate.now());
            idle.setStartTime(LocalTime.now());
            idle.setUser(user);
            idle.setName("Inactive");
            activityRepository.save(idle);
        }

        response.sendRedirect(request.getContextPath() + "/app/main");
        return null;
    }

    @PostMapping("/app/main/activate")
    public String postActivateTask(@Valid Activity activity, BindingResult result, Model model,
                               HttpServletRequest request, HttpServletResponse response ) throws Exception {
        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long)session.getAttribute("id"));

        Activity active = activityRepository.findActiveOne(user);
        if (active != null) {
            active.setEndTime(LocalTime.now());
            active.setDuration(Duration.between(active.getStartTime(), active.getEndTime()));
            activityRepository.save(active);
        }

        activity.setName(activity.getTask().getName());
        activity.setUser(user);
        activity.setDate(LocalDate.now());
        activity.setStartTime(LocalTime.now());
        activityRepository.save(activity);

        response.sendRedirect(request.getContextPath() + "/app/main");
        return null;
    }

}
