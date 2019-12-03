package pl.coderslab.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.Duration;
import java.util.List;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String name;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    private String type;

    private Duration duration;

    @Nullable
    @ManyToOne
    @JoinColumn(name = "bauArchetype_id")
    private BauReport bauArchetype;

    @Nullable
    private String description;

    @OneToMany(mappedBy = "task")
    private List<Activity> activities;

}
