package dev.matheuslf.desafio.inscritos.models.repository;


import dev.matheuslf.desafio.inscritos.models.entities.TaskModel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskModel, UUID> {
    List<TaskModel> findAll(Specification<TaskModel> spec);

}
