package pl.coderslab.controllers.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.dto.TaskReservationDto;
import pl.coderslab.entities.*;
import pl.coderslab.repositories.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
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
        for (Task task : taskSet.get(1)) {
            task.setName(task.getClient().getName() + " - " + task.getName());
        }
        model.addAttribute("reservedTasks", taskSet.get(1));

        Activity workingHours = activityRepository.findWorkingHours(user);
        if (workingHours != null) {
            model.addAttribute("startTime", workingHours.getStartTime());
            model.addAttribute("endTime", workingHours.getEndTime());
        } else {
            model.addAttribute("startTime", "");
            model.addAttribute("endTime", "");
        }
        model.addAttribute("activity", new Activity());
        model.addAttribute("presentActivity", activityRepository.findActiveOne(user));
        return "app/displayUserPanel";
    }

    private List<List<Task>> getDailyTaskList(User user) {

        //remove old reservations
        List<Task> unclompletedTasks = null;
        if (taskRepository.findAllUncompletedAssignedBeforeToday() != null) {
           unclompletedTasks = taskRepository.findAllUncompletedAssignedBeforeToday();
        }
        for (Task task : unclompletedTasks) {
            task.setUser(null);
            taskRepository.save(task);
        }

        List<Task> tasks = new ArrayList<>();
        List<Task> reservedTasks = new ArrayList<>();

        for (Client client: user.getClients()) {
            client = clientRepository.findClientWithBauReports(client.getId());

            for (BauReport bauReport : client.getBauReportList()) {

                if (isReportDueToday(bauReport)) {
                    //check if there is no previously created uncompleted task
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

            }
            tasks.addAll(taskRepository.findTasksByClient(client));
            reservedTasks.addAll(taskRepository.findReservedTasksByClient(client, user));
        }
        return Arrays.asList(tasks, reservedTasks);
    }

    boolean isReportDueToday(BauReport report) {

        if (report.getFrequency().equals("Dzienny")) {
            return true;
        } else if (report.getFrequency().equals("Tygodniowy")) {
            if (report.getRunDay().equals("Poniedziałek")) {
                report.setRunDay("MONDAY");
            } else if (report.getRunDay().equals("Wtorek")) {
                report.setRunDay("TUESDAY");
            } else if (report.getRunDay().equals("Środa")) {
                report.setRunDay("WEDNESDAY");
            } else if (report.getRunDay().equals("Czwartek")) {
                report.setRunDay("THURSDAY");
            } else {
                report.setRunDay("FRIDAY");
            }
            String today = LocalDate.now().getDayOfWeek().toString();
            if (today.equals(report.getRunDay())) {
                return true;
            }
        } else {
            int today = LocalDate.now().getDayOfMonth();
            if (today == Integer.valueOf(report.getRunDay())) {
                return true;
            }
        }
        return false;
    }

    @GetMapping("/app/userPanel/assignTask/{taskId}")
    @ResponseBody
    public TaskReservationDto getAssignTask(@PathVariable long taskId,
                                            HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long)session.getAttribute("id"));
        Task task = taskRepository.findOne(taskId);
        TaskReservationDto taskReservationDto = new TaskReservationDto();
        if (task.getUser() == null) {
            task.setUser(user);
            task.setDateAssigned(LocalDate.now());
            taskRepository.save(task);


            taskReservationDto.setType("assignTask");
            taskReservationDto.setUserName(user.getFullName());
            taskReservationDto.setTaskId(task.getId());
            taskReservationDto.setDropdownTasks(getDropdownTasks(request));

        }
        return taskReservationDto;
    }

    @GetMapping("/app/userPanel/unassignTask/{taskId}")
    @ResponseBody
    public TaskReservationDto getUnassignTask(@PathVariable long taskId,
                                HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long)session.getAttribute("id"));
        Task task = taskRepository.findOne(taskId);
        TaskReservationDto taskReservationDto = new TaskReservationDto();
        if (task.getUser().getId() == user.getId()) {
            task.setUser(null);
            task.setDateAssigned(null);
            taskRepository.save(task);

            taskReservationDto.setType("unassignTask");
            taskReservationDto.setTaskId(task.getId());
            taskReservationDto.setDropdownTasks(getDropdownTasks(request));

        }
        return taskReservationDto;
    }

    @GetMapping("/app/userPanel/createAdHoc")
    public String getCreateAdHoc(HttpServletRequest request, Model model) {
        model.addAttribute("task", new Task());
        return "app/createAdHoc";
    }

    @PostMapping("/app/userPanel/createAdHoc")
    public String postCreateAdHoc(@Valid Task task, BindingResult result,
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


    @GetMapping("/app/userPanel/getDropdownTasks")
    @ResponseBody
    public Object[][] getDropdownTasks(HttpServletRequest request) {

        HttpSession session = request.getSession();
        User user = userRepository.findOneWithClients((long)session.getAttribute("id"));

        List<List<Task>> taskSet = getDailyTaskList(user);
        Object[][] dropdownTasks = new Object[taskSet.get(1).size()][2];
        for (int i = 0; i < taskSet.get(1).size(); i++) {
            dropdownTasks[i][0] = taskSet.get(1).get(i).getId();
            dropdownTasks[i][1] = taskSet.get(1).get(i).getClient().getName() + " - " + taskSet.get(1).get(i).getName();
        }
        return dropdownTasks;
    }

}
