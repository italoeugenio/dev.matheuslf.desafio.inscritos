package dev.matheuslf.desafio.inscritos.models.entities;


import dev.matheuslf.desafio.inscritos.enums.TaskPriority;
import dev.matheuslf.desafio.inscritos.enums.TaskStatus;
import dev.matheuslf.desafio.inscritos.models.dtos.TaskRequestDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "TB_TASKS")
public class TaskModel {
    private static final long serialVersion = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "taks_id")
    private UUID id;

    @Column(name = "title")
    @Size(min = 5, max = 150, message = "The min length is 5 and max is 150 to task title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @Column(name = "due_time")
    private LocalDateTime dueTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private ProjectModel project;

    public TaskModel(TaskRequestDTO data){
        this.title = data.title();
        this.description = data.description();
        this.status = data.status();
        this.priority = data.priority();
        this.dueTime = data.dueDate();
    }
}
