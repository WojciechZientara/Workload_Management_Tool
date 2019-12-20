package pl.coderslab.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.entities.Role;
import pl.coderslab.entities.User;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findRoleByName(String name);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_role VALUES (?1, ?2)", nativeQuery = true)
    void createUserRoleAssociation(long userId, long roleId);

}
