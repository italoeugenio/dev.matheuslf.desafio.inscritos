package dev.matheuslf.desafio.inscritos.models.service;

import dev.matheuslf.desafio.inscritos.enums.TaskPriority;
import dev.matheuslf.desafio.inscritos.enums.TaskStatus;
import dev.matheuslf.desafio.inscritos.exceptions.project.ProjectException;
import dev.matheuslf.desafio.inscritos.exceptions.project.ProjectNotFoundException;
import dev.matheuslf.desafio.inscritos.models.dtos.ProjectRequestDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.ProjectResponseDetailsDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.TaskRequestDTO;
import dev.matheuslf.desafio.inscritos.models.entities.ProjectModel;
import dev.matheuslf.desafio.inscritos.models.entities.TaskModel;
import dev.matheuslf.desafio.inscritos.models.repository.ProjectRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

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
    void saveProjectCase1() {
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
Ø
        verify(projectRepository, times(1)).save(any(ProjectModel.class));
    }

    @Test
    @DisplayName("Should get product by id")
    void getByIdCase1() {
        ProjectModel project = new ProjectModel();
        LocalDateTime now = LocalDateTime.now();

        project.setId(UUID.randomUUID());
        project.setName("Project Test");
        project.setTasks(new ArrayList<>());
        project.setDescription("This is a description to project");
        project.setStartDate(now);
        project.setEndDate(now.plusMinutes(5));

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        ProjectResponseDetailsDTO result = projectService.getById(project.getId());

        assertThat(result).isNotNull();
        assertThat(result.startDate()).isBefore(result.endDate());

        verify(projectRepository, times(1)).findById(project.getId());
    }

    @Test
    @DisplayName("Should throw a exception when a project is not found")
    void getByIdCase2() {
        UUID projectId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ProjectNotFoundException.class, () -> projectService.getById(projectId)
        );

        assertTrue(exception.getMessage().contains("Project not found"));
    }


    @Test
    @DisplayName("Should delete a project")
    void deleteProjectCase1() {
        ProjectModel project = new ProjectModel();
        LocalDateTime now = LocalDateTime.now();

        project.setId(UUID.randomUUID());
        project.setName("Project Test");
        project.setTasks(new ArrayList<>());
        project.setDescription("This is a description to project");
        project.setStartDate(now);
        project.setEndDate(now.plusMinutes(5));

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        projectService.deleteProject(project.getId());


        verify(projectRepository, times(1)).findById(project.getId());
        verify(projectRepository, times(1)).deleteById(project.getId());
    }

    @Test
    @DisplayName("Should throw a exception when don't found the project ")
    void deleteProjectCase2() {
        UUID projectId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ProjectNotFoundException.class, () -> projectService.deleteProject(projectId)
        );

        assertTrue(exception.getMessage().contains("Project not found"));
    }

    @Test
    @DisplayName("Should update a project")
    void updateProjectCase1() {
        ProjectModel project = new ProjectModel();
        LocalDateTime now = LocalDateTime.now();

        project.setId(UUID.randomUUID());
        project.setName("Project Test");
        project.setTasks(new ArrayList<>());
        project.setDescription("This is a description to project");
        project.setStartDate(now);
        project.setEndDate(now.plusMinutes(5));

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        ProjectRequestDTO data = new ProjectRequestDTO("New name", "New Description", project.getStartDate().plusMinutes(5),project.getEndDate().plusMinutes(5));

        projectService.updateProject(project.getId(),data);

        verify(projectRepository, times(1)).findById(project.getId());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    @DisplayName("Should throw a exception when project not found")
    void updateProjectCase2() {
        UUID projectId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        ProjectRequestDTO data = new ProjectRequestDTO("New name", "New Description", LocalDateTime.now().plusMinutes(5),LocalDateTime.now().plusMinutes(5));

        Exception exception = assertThrows(
                ProjectNotFoundException.class, () -> projectService.updateProject(projectId,data)
        );

        assertThat(exception.getMessage()).isEqualTo("Project not found");
    }

    @Test
    @DisplayName("Should throw a exception when the new date of project are not compatible with taks dates")
    void updateProjectCase3() {
        ProjectModel project = new ProjectModel();
        LocalDateTime now = LocalDateTime.now();

        project.setId(UUID.randomUUID());
        project.setName("Project Test");
        project.setTasks(new ArrayList<>());
        project.setDescription("This is a description to project");
        project.setStartDate(now);
        project.setEndDate(now.plusHours(5));

        TaskRequestDTO data1 = new TaskRequestDTO(
                "Task 1",
                "Task description 1",
                TaskStatus.DOIN,
                TaskPriority.LOW,
                project.getStartDate().plusMinutes(30),
                project.getId());

        TaskRequestDTO data2 = new TaskRequestDTO(
                "Task 2",
                "Task description 2",
                TaskStatus.DOIN,
                TaskPriority.LOW,
                project.getStartDate().plusHours(2),
                project.getId());

        TaskRequestDTO data3 = new TaskRequestDTO(
                "Task 3",
                "Task description 3",
                TaskStatus.DOIN,
                TaskPriority.LOW,
                project.getStartDate().plusHours(4),
                project.getId());

        TaskModel task1 = new TaskModel(data1);
        TaskModel task2 = new TaskModel(data2);
        TaskModel task3 = new TaskModel(data3);

        project.getTasks().add(task1);
        project.getTasks().add(task2);
        project.getTasks().add(task3);

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        TaskModel minTaskByDueTime = null;
        TaskModel maxTaskByDueTime = null;

        for (TaskModel task : project.getTasks()) {
            if (minTaskByDueTime == null || task.getDueTime().isBefore(minTaskByDueTime.getDueTime())) {
                minTaskByDueTime = task;
            }
            if (maxTaskByDueTime == null || task.getDueTime().isAfter(maxTaskByDueTime.getDueTime())) {
                maxTaskByDueTime = task;
            }
        }

        ProjectRequestDTO projectData = new ProjectRequestDTO(
                "New Project name",
                "New project description",
                project.getStartDate().plusDays(1),
                project.getEndDate().plusDays(5));

        Exception exception = assertThrows(
                ProjectException.class, () -> projectService.updateProject(project.getId(), projectData)
        );


        String exceptionMessage = String.format(
                "Cannot update project dates. Your tasks span from %s to %s. " +
                        "Please set start date on or before %s and end date on or after %s" +
                        " (Earliest task id: %s, Latest task id: %s)",
                minTaskByDueTime.getDueTime(), maxTaskByDueTime.getDueTime(),
                minTaskByDueTime.getDueTime(), maxTaskByDueTime.getDueTime(),
                minTaskByDueTime.getId(), maxTaskByDueTime.getId()
        );

        assertThat(exception.getMessage()).isEqualTo(exceptionMessage);
    }


    @Test
    @DisplayName("Should throw a exception when the end date can't be before start date")
    void validateProjectDatesCase1() {
        ProjectRequestDTO data = new ProjectRequestDTO(
                "Project name",
                "Project Descritpion",
                LocalDateTime.now().plusMinutes(1),
                LocalDateTime.now().minusDays(1)
        );

        Exception exception = assertThrows(
                ProjectException.class, () -> projectService.validateProjectDates(data)
        );

        assertThat(exception.getMessage()).isEqualTo("The end date can't be before start date");
    }

    @Test
    @DisplayName("Should throw a exception when the start date can't be one that has passed")
    void validateProjectDatesCase2() {
        ProjectRequestDTO data = new ProjectRequestDTO(
                "Project name",
                "Project Descritpion",
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now().plusDays(1)
        );

        Exception exception = assertThrows(
                ProjectException.class, () -> projectService.validateProjectDates(data)
        );

        assertThat(exception.getMessage()).isEqualTo("The start date can't be one that has passed");
    }


}