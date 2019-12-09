package pl.coderslab.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.coderslab.entities.Activity;
import pl.coderslab.entities.User;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("SELECT DISTINCT a FROM Activity a LEFT JOIN FETCH a.task WHERE a.user = ?1 ORDER BY a.date, a.startTime")
    List<Activity> findActivitiesByUser(User user);

    @Query("SELECT a FROM Activity a WHERE a.user = ?1 AND a.name = 'Working Hours' AND a.date = CURRENT_DATE")
    List<Activity> findWorkingHours(User user);

    @Query("SELECT a FROM Activity a WHERE a.user = ?1 AND a.endTime = null AND a.name <> 'Working Hours'")
    Activity findActiveOne (User user);


}
