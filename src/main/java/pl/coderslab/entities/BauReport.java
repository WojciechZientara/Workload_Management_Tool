package pl.coderslab.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "bau_reports")
@Getter @Setter
@NoArgsConstructor
public class BauReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String name;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @NotNull
    private Client client;

    private long sumOfDuration;

    private long numberOfRuns;

    private long averageDuration;

    private String frequency;

    private String runDay;

}
