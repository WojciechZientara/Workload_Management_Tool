package pl.coderslab.controllers.app;

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
import java.util.Arrays;
import java.util.List;

@Controller
public class UserPanelController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    ActivityRepository activityRepository;

    @ModelAttribute("clients")
    public List<Client> getClients() {
        return clientRepository.findAll();
    }

    @GetMapping("/app/userPanel")
    public String getUserPanel(Model model,
                          HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long)session.getAttribute("id"));
        List<List<Task>> taskSet = getDailyTaskList(user);
        model.addAttribute("tasks", taskSet.get(0));
        model.addAttribute("reservedTasks", taskSet.get(1));
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
        return "app/displayUserPanel";
    }

    private List<List<Task>> getDailyTaskList(User user) {
        List<Task> tasks = new ArrayList<>();
        List<Task> reservedTasks = new ArrayList<>();

        for (Client client: user.getClients()) {
            client = clientRepository.findClientWithBauReports(client.getId());

            for (BauReport bauReport : client.getBauReportList()) {
                if (taskRepository.findTaskByBauReport(bauReport) == null &&
                        taskRepository.findTaskByBauReportCompletedToday(bauReport) == null) {
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
            reservedTasks.addAll(taskRepository.findReservedTasksByClient(client, user));
        }
        return Arrays.asList(tasks, reservedTasks);
    }

    @GetMapping("/app/userPanel/assignTask/{taskId}")
    public String getAssignTask(@PathVariable long taskId, Model model,
                                HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession();
        User user = userRepository.findOne((long)session.getAttribute("id"));
        Task task = taskRepository.findOne(taskId);
        if (task.getUser() == null) {
            task.setUser(user);
            task.setDateAssigned(LocalDate.now());
            taskRepository.save(task);
        }
        response.sendRedirect(request.getContextPath() + "/app/userPanel");
        return null;

    }

    @GetMapping("/app/userPanel/createAdHoc")
    public String getCreateAdHoc(HttpServletRequest request, Model model) {
        model.addAttribute("task", new Task());
        return "app/createAdHoc";
    }

    @PostMapping("/app/userPanel/createAdHoc")
    public String postCreateAdHoc(@Valid Task task, BindingResult result, Model model,
                                HttpServletRequest request, HttpServletResponse response ) throws IOException {

        try{
            if (result.hasErrors()) {
                return "app/createAdHoc";
            } else {
                task.setType("Ad-hoc");
                task.setEstimatedDuration(task.getEstimatedDuration() * 60);
                task.setActivities(new ArrayList<>());
                taskRepository.save(task);
                response.sendRedirect(request.getContextPath() + "/app/userPanel");
            }
        } catch (Exception e) {
            return "app/createAdHoc";
        }
        return null;
    }


}
