package dev.matheuslf.desafio.inscritos.models.repository;

import dev.matheuslf.desafio.inscritos.models.entities.ProjectModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<ProjectModel, UUID> {

}
