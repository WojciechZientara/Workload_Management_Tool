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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    BauReportRepository bauReportRepository;

    @Autowired
    ActivityRepository activityRepository;

    @ModelAttribute("clients")
    public List<Client> getClients() {
        return clientRepository.findAll();
    }

    @GetMapping("/app/main")
    public String getMain(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        if ((boolean)session.getAttribute("admin")) {
            return "app/mainDashboard";
        } else {
            User user = userRepository.findOneWithClients((long)session.getAttribute("id"));
            model.addAttribute("tasks", getDailyTaskList(user));
            try {
                Activity workingHours = activityRepository.findWorkingHours(user).get(0);
                model.addAttribute("startTime", workingHours.getStartTime());
                model.addAttribute("endTime", workingHours.getEndTime());
            } catch (Exception e) {
                model.addAttribute("startTime", "");
                model.addAttribute("endTime", "");
            }
            model.addAttribute("activity", new Activity());
            model.addAttribute("presentActivity", activityRepository.findActiveOne(user));
            return "app/mainCockpit";
        }
    }

    @GetMapping("/app/main/assignTask/{taskId}")
    public String getAssignTask(@PathVariable long taskId, Model model,
                                HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession();
        User user = userRepository.findOne((long)session.getAttribute("id"));
        Task task = taskRepository.findOne(taskId);
        task.setUser(user);
        taskRepository.save(task);
        response.sendRedirect(request.getContextPath() + "/app/main");
        return null;

    }

    private List<Task> getDailyTaskList(User user) {
        List<Task> tasks = new ArrayList<>();

        for (Client client: user.getClients()) {
            client = clientRepository.findClientWithBauReports(client.getId());

            for (BauReport bauReport : client.getBauReportList()) {
                if (taskRepository.findTaskByBauReport(bauReport) == null) {
                    Task task = new Task();
                    task.setName(bauReport.getName());
                    task.setClient(client);
                    task.setType("BAU");
                    task.setBauArchetype(bauReport);
                    task.setEstimatedDuration(bauReport.getAverageDuration());
                    task.setActivities(new ArrayList<>());
                    taskRepository.save(task);
                }
            }
            tasks.addAll(taskRepository.findTasksByClient(client));
        }
        return tasks;
    }

    @GetMapping("/app/main/newAdHoc")
    public String getNewAdHoc(HttpServletRequest request, Model model) {
        model.addAttribute("task", new Task());
        return "app/newAdHoc";
    }

    @PostMapping("/app/main/newAdHoc")
    public String postNewAdHoc(@Valid Task task, BindingResult result, Model model,
                                HttpServletRequest request, HttpServletResponse response ) throws IOException {

        try{
            if (result.hasErrors()) {
                return "app/newAdHoc";
            } else {
                task.setType("Ad-hoc");
                task.setActivities(new ArrayList<>());
                taskRepository.save(task);
                response.sendRedirect(request.getContextPath() + "/app/main");
            }
        } catch (Exception e) {
            return "app/newAdHoc";
        }
        return null;
    }


}
