package pl.coderslab.entities;

import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String password;

    private boolean isAdmin;

    private boolean isPasswordChanged;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @ManyToMany(mappedBy = "users")
    private List<Client> clients = new ArrayList<>();

    private int dailyWorkingHours;

    @OneToMany(mappedBy = "user")
    private List<Activity> activities;

    public void setPassword(String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

}
