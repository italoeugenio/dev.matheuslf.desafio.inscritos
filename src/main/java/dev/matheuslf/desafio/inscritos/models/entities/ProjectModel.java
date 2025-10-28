package dev.matheuslf.desafio.inscritos.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.matheuslf.desafio.inscritos.models.dtos.ProjectRequestDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "TB_PROJECTS")
public class ProjectModel {
    private static final long serialVersion = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "project_id")
    private UUID id;

    @Column(name = "name", nullable = false)
    @Size(min = 3, max = 100, message = "The min length is 3 and the max is 100 to project name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TaskModel> tasks = new ArrayList<>();

    public ProjectModel(ProjectRequestDTO data){
        this.name = data.name();
        this.description = data.description();
        this.startDate = data.startDate();
        this.endDate = data.endDate();
    }
}
