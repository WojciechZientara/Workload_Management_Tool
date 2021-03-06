package pl.coderslab.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.entities.User;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByEmail(String email);

    @Query(value = "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.clients ORDER BY u.id")
    List<User> findAllWithClients();

    @Query(value = "SELECT u FROM User u LEFT JOIN FETCH u.clients WHERE u.id = ?1")
    User findOneWithClients(long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_role WHERE user_id = ?1", nativeQuery = true)
    void clearUserRoleAssociations(long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM clients_users WHERE users_id = ?1", nativeQuery = true)
    void clearUsersClientAssociations(long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tasks WHERE user_id = ?1", nativeQuery = true)
    void clearUserTasksAssociations(long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM activities WHERE user_id = ?1", nativeQuery = true)
    void clearUserActivitiesAssociations(long id);

}
