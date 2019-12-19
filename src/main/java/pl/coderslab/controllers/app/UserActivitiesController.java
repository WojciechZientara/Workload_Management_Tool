package pl.coderslab.controllers.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.entities.*;
import pl.coderslab.repositories.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class UserActivitiesController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ActivityRepository activityRepository;

    @GetMapping("/app/userActivities")
    public String getUserActivities(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long)session.getAttribute("id"));
        List<Activity> activities = activityRepository.findActivitiesByUser(user);
        model.addAttribute("activities", activities);
        return "app/displayUserActivities";
    }

}
