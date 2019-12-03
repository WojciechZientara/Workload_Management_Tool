package pl.coderslab.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String name;

    @ManyToMany
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.REMOVE)
    private List<BauReport> bauReportList;

}
