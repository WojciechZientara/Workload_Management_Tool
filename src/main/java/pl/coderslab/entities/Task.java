package pl.coderslab.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tasks")
@Getter @Setter
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

    @NumberFormat
    private long estimatedDuration;

    private long duration;

    @Nullable
    @ManyToOne
    @JoinColumn(name = "bauArchetype_id")
    private BauReport bauArchetype;

    @Nullable
    private String description;

    @OneToMany(mappedBy = "task")
    private List<Activity> activities;

    @Nullable
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private boolean isCompleted = false;

    private LocalDate dateAssigned;

    private LocalDate dateCompleted;

}
