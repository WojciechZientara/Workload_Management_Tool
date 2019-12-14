package pl.coderslab.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.entities.BauReport;
import pl.coderslab.entities.Client;
import pl.coderslab.entities.Task;
import pl.coderslab.entities.User;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.isCompleted = false AND t.user <> null AND t.dateAssigned = CURRENT_DATE ")
    List<Task> findAllActiveAssignedTasks();

    @Query("SELECT t FROM Task t WHERE t.dateCompleted = CURRENT_DATE")
    List<Task> findAllTasksCompletedToday();

    @Query("SELECT t FROM Task t WHERE t.bauArchetype = ?1 AND t.isCompleted = false ")
    Task findTaskByBauReport(BauReport bauReport);

    @Query("SELECT t FROM Task t WHERE t.bauArchetype = ?1 AND t.dateCompleted = CURRENT_DATE ")
    Task findTaskByBauReportCompletedToday(BauReport bauReport);

    @Query("SELECT t FROM Task t WHERE t.client = ?1 AND t.isCompleted = false ")
    List<Task> findTasksByClient(Client client);

    @Query("SELECT t FROM Task t WHERE t.client = ?1 AND t.user = ?2 AND t.isCompleted = false ")
    List<Task> findReservedTasksByClient(Client client, User user);

    @Query("SELECT t.name FROM Task t WHERE t.isCompleted = false AND t.user <> null GROUP BY t.name")
    List<String> findAllAssignedTasksNames();

    @Query("SELECT t.user FROM Task t WHERE t.isCompleted = false AND t.user <> null GROUP BY t.user")
    List<User> findAllAssignedTasksUsers();

}
