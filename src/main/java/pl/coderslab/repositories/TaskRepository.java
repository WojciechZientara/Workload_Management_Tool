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

    @Query("SELECT t FROM Task t WHERE t.bauArchetype = ?1")
    Task findTaskByBauReport(BauReport bauReport);

    @Query("SELECT t FROM Task t WHERE t.client = ?1")
    List<Task> findTasksByClient(Client client);

}
