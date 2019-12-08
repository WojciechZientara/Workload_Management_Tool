package pl.coderslab.entities;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.lang.NonNullApi;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
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
    @NotNull
    private Client client;

    private long sumOfDuration;

    private long numberOfRuns;

    private long averageDuration;

}
