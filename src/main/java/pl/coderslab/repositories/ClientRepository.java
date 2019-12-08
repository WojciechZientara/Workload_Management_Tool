package pl.coderslab.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.entities.Client;
import pl.coderslab.entities.User;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query(value = "SELECT DISTINCT c FROM Client c LEFT JOIN FETCH c.users ORDER BY c.id")
    List<Client> findAllWithUsers();

    @Query("SELECT DISTINCT c FROM Client c LEFT JOIN FETCH c.users WHERE c.id = ?1")
    Client findClientWithUsers(long id);

    @Query("SELECT DISTINCT c FROM Client c LEFT JOIN FETCH c.bauReportList WHERE c.id = ?1")
    Client findClientWithBauReports(long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM clients_users WHERE clients_id = ?1", nativeQuery = true)
    void clearClientsUsersAssociations(long id);




}
