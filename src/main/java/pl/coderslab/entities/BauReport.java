package pl.coderslab.entities;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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
    private Client client;

    private Duration sumOfDuration;

    private long numberOfRuns;

    private Duration averageDuration;

}
