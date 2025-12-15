package dev.matheuslf.desafio.inscritos.models.service;

import dev.matheuslf.desafio.inscritos.models.dtos.ProjectRequestDTO;
import dev.matheuslf.desafio.inscritos.models.entities.ProjectModel;
import dev.matheuslf.desafio.inscritos.models.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Should save a project in data")
    void saveProject() {
        LocalDateTime now = LocalDateTime.now();
        ProjectRequestDTO validProjectRequest = new ProjectRequestDTO(
                "Project Name",
                "Project Description",
                now.plusDays(1),
                now.plusDays(10)
        );
        ProjectModel projectModel = new ProjectModel(validProjectRequest);
        projectModel.setId(UUID.randomUUID());

        when(projectRepository.save(any(ProjectModel.class))).thenReturn(projectModel);

        ProjectModel result = projectService.saveProject(validProjectRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(validProjectRequest.name());
        assertThat(result.getDescription()).isEqualTo(validProjectRequest.description());

        verify(projectRepository, times(1)).save(any(ProjectModel.class));
    }

    @Test
    void getByIdCase1() {

    }

    @Test
    void getByIdCase2() {
    }


    @Test
    void deleteProject() {
    }

    @Test
    void updateProject() {
    }

    @Test
    void validateProjectDates() {
    }
}