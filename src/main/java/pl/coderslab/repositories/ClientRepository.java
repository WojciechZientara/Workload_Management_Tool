package pl.coderslab.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.coderslab.entities.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT DISTINCT c FROM Client c LEFT JOIN FETCH c.users WHERE c.id = ?1")
    Client findClientWithUsers(long id);

    @Query("SELECT DISTINCT c FROM Client c LEFT JOIN FETCH c.bauReportList WHERE c.id = ?1")
    Client findClientWithBauReports(long id);


}
