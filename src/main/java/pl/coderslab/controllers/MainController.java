package pl.coderslab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.coderslab.entities.*;
import pl.coderslab.repositories.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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

    @GetMapping("/app/main")
    public String getMain(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        if ((boolean)session.getAttribute("admin")) {
            return "app/mainDashboard";
        } else {
            User user = userRepository.findOneWithClients((long)session.getAttribute("id"));
            model.addAttribute("tasks", getDailyTaskList(user));
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
                    task.setActivities(new ArrayList<>());
                    taskRepository.save(task);
                }
            }
            tasks.addAll(taskRepository.findTasksByClient(client));
        }
        return tasks;
    }

}
