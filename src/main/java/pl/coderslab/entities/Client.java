package pl.coderslab.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
@Getter @Setter
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String name;

    @ManyToMany
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.MERGE)
    private List<BauReport> bauReportList;

}
