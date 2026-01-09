package dev.matheuslf.desafio.inscritos.models.service;

import dev.matheuslf.desafio.inscritos.enums.TaskPriority;
import dev.matheuslf.desafio.inscritos.enums.TaskStatus;
import dev.matheuslf.desafio.inscritos.exceptions.project.ProjectNotFoundException;
import dev.matheuslf.desafio.inscritos.exceptions.task.TaskException;
import dev.matheuslf.desafio.inscritos.exceptions.task.TaskNotFoundException;
import dev.matheuslf.desafio.inscritos.models.dtos.TaskRequestDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.TaskResponseDetailsDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.TaskStatusUpdateRequestDTO;
import dev.matheuslf.desafio.inscritos.models.entities.ProjectModel;
import dev.matheuslf.desafio.inscritos.models.entities.TaskModel;
import dev.matheuslf.desafio.inscritos.models.repository.ProjectRepository;
import dev.matheuslf.desafio.inscritos.models.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Should create a task")
    void addTaskCase1() {
        ProjectModel projectModel = new ProjectModel(
                UUID.randomUUID(),
                "Project Name",
                "Project Description",
                LocalDateTime.now().plusMinutes(1),
                LocalDateTime.now().plusDays(1),
                new ArrayList<>()
        );

        TaskRequestDTO data = new TaskRequestDTO(
                "Task title",
                "Task description",
                TaskStatus.TODO,
                TaskPriority.LOW,
                projectModel.getEndDate().minusMinutes(1),
                projectModel.getId()

        );

        TaskModel taskModel = new TaskModel(data);
        taskModel.setProject(projectModel);

        when(projectRepository.findById(projectModel.getId())).thenReturn(Optional.of(projectModel));
        when(taskRepository.save(any(TaskModel.class))).thenReturn(taskModel);

        TaskModel result = taskService.addTask(data);

        verify(projectRepository, times(1)).findById(projectModel.getId());
        verify(taskRepository, times(1)).save(any(TaskModel.class));

        assertThat(result).isNotNull();
        assertThat(result.getProject()).isEqualTo(projectModel);
    }

    @Test
    @DisplayName("Should throw an exception when project is not found")
    void addTaskCase2() {
        UUID projectId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        ProjectModel projectModel = new ProjectModel(
                UUID.randomUUID(),
                "Project Name",
                "Project Description",
                LocalDateTime.now().plusMinutes(1),
                LocalDateTime.now().plusDays(1),
                new ArrayList<>()
        );

        TaskRequestDTO data = new TaskRequestDTO(
                "Task title",
                "Task description",
                TaskStatus.TODO,
                TaskPriority.LOW,
                projectModel.getEndDate().minusMinutes(1),
                projectModel.getId()

        );

        Exception exception = assertThrows(
                ProjectNotFoundException.class, () -> taskService.addTask(data)
        );

        assertThat(exception.getMessage()).isEqualTo("Project not found");
    }

    @Test
    @DisplayName("Should throw an exception when task due date is after project end date")
    void addTaskCase3() {
        ProjectModel project = new ProjectModel();
        project.setId(UUID.randomUUID());
        project.setName("Project Test");
        project.setDescription("Description");
        project.setStartDate(LocalDateTime.now());
        project.setEndDate(LocalDateTime.now().plusDays(10));

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        TaskRequestDTO taskData = new TaskRequestDTO(
                "Task 1",
                "Task description",
                TaskStatus.TODO,
                TaskPriority.HIGH,
                project.getEndDate().plusDays(1),
                project.getId()
        );

        TaskException exception = assertThrows(
                TaskException.class,
                () -> taskService.addTask(taskData)
        );

        assertThat(exception.getMessage())
                .isEqualTo("The task's due date must be between the project's start and end dates");

        verify(projectRepository, times(1)).findById(project.getId());
        verify(taskRepository, never()).save(any(TaskModel.class));
    }

    @Test
    @DisplayName("Should throw an exception when task due date is before project start date")
    void addTaskCase4() {
        ProjectModel project = new ProjectModel();
        project.setId(UUID.randomUUID());
        project.setName("Project Test");
        project.setDescription("Description");
        project.setStartDate(LocalDateTime.now().plusDays(1));
        project.setEndDate(LocalDateTime.now().plusDays(10));

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        TaskRequestDTO taskData = new TaskRequestDTO(
                "Task 1",
                "Task description",
                TaskStatus.TODO,
                TaskPriority.HIGH,
                project.getStartDate().minusDays(1),
                project.getId()
        );

        TaskException exception = assertThrows(
                TaskException.class,
                () -> taskService.addTask(taskData)
        );

        assertThat(exception.getMessage())
                .isEqualTo("The task's due date must be between the project's start and end dates");

        verify(projectRepository, times(1)).findById(project.getId());
        verify(taskRepository, never()).save(any(TaskModel.class));
    }

    @Test
    @DisplayName("Should return all tasks")
    void getAllCase1() {
        ProjectModel project = new ProjectModel(
                UUID.randomUUID(),
                "Project Name",
                "Project Description",
                LocalDateTime.now().plusMinutes(1),
                LocalDateTime.now().plusDays(1),
                new ArrayList<>()
        );

        TaskModel task1 = new TaskModel();
        task1.setId(UUID.randomUUID());
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setStatus(TaskStatus.TODO);
        task1.setProject(project);

        TaskModel task2 = new TaskModel();
        task2.setId(UUID.randomUUID());
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setStatus(TaskStatus.TODO);
        task2.setProject(project);

        List<TaskModel> taskList = List.of(task1, task2);

        when(taskRepository.findAll()).thenReturn(taskList);

        List<TaskResponseDetailsDTO> result = taskService.getAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo("Task 1");
        assertThat(result.get(1).title()).isEqualTo("Task 2");

        verify(taskRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no tasks exist")
    void getAllCase2() {
        when(taskRepository.findAll()).thenReturn(new ArrayList<>());

        List<TaskResponseDetailsDTO> result = taskService.getAll();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(taskRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return a Task by id")
    void getByIdCase1() {
        ProjectModel projectModel = new ProjectModel(
                UUID.randomUUID(),
                "Project Name",
                "Project Description",
                LocalDateTime.now().plusMinutes(1),
                LocalDateTime.now().plusDays(1),
                new ArrayList<>()
        );

        TaskRequestDTO data = new TaskRequestDTO(
                "Task title",
                "Task description",
                TaskStatus.TODO,
                TaskPriority.LOW,
                projectModel.getEndDate().minusMinutes(1),
                projectModel.getId()

        );

        TaskModel taskModel = new TaskModel(data);
        taskModel.setProject(projectModel);

        when(taskRepository.findById(taskModel.getId())).thenReturn(Optional.of(taskModel));

        TaskResponseDetailsDTO result = taskService.getById(taskModel.getId());

        assertThat(result).isNotNull();
        assertThat(result.description()).isEqualTo(taskModel.getDescription());

        verify(taskRepository, times(1)).findById(taskModel.getId());
    }

    @Test
    @DisplayName("Should throw a exception when don't found the task")
    void getByIdCase2() {
        UUID taskId = UUID.randomUUID();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                TaskNotFoundException.class, () -> taskService.getById(taskId)
        );

        assertThat(exception.getMessage()).isEqualTo("Task not found");
    }

    @Test
    @DisplayName("Should delete a task by id")
    void deleteTaskCase1() {
        ProjectModel projectModel = new ProjectModel(
                UUID.randomUUID(),
                "Project Name",
                "Project Description",
                LocalDateTime.now().plusMinutes(1),
                LocalDateTime.now().plusDays(1),
                new ArrayList<>()
        );

        TaskRequestDTO data = new TaskRequestDTO(
                "Task title",
                "Task description",
                TaskStatus.TODO,
                TaskPriority.LOW,
                projectModel.getEndDate().minusMinutes(1),
                projectModel.getId()

        );

        TaskModel taskModel = new TaskModel(data);
        taskModel.setProject(projectModel);

        when(taskRepository.findById(taskModel.getId())).thenReturn(Optional.of(taskModel));

        taskService.deleteTask(taskModel.getId());

        verify(taskRepository, times(1)).findById(taskModel.getId());
        assertThat(taskModel).isNotNull();

    }

    @Test
    @DisplayName("Should throw a exception when don't found the task")
    void updateTaskCase2() {
        UUID taskId = UUID.randomUUID();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                TaskNotFoundException.class, () -> taskService.deleteTask(taskId)
        );

        assertThat(exception.getMessage()).isEqualTo("Task not found");
    }

    @Test
    @DisplayName("Should update task status")
    void updateTaskStatusCase1() {
        ProjectModel projectModel = new ProjectModel(
                UUID.randomUUID(),
                "Project Name",
                "Project Description",
                LocalDateTime.now().plusMinutes(1),
                LocalDateTime.now().plusDays(1),
                new ArrayList<>()
        );

        TaskRequestDTO data = new TaskRequestDTO(
                "Task title",
                "Task description",
                TaskStatus.TODO,
                TaskPriority.LOW,
                projectModel.getEndDate().minusMinutes(1),
                projectModel.getId()
        );

        TaskStatusUpdateRequestDTO dataUpdateTask = new TaskStatusUpdateRequestDTO(
                TaskStatus.DOIN
        );

        TaskModel taskModel = new TaskModel(data);
        taskModel.setProject(projectModel);

        when(taskRepository.findById(taskModel.getId())).thenReturn(Optional.of(taskModel));

        taskService.updateTaskStatus(taskModel.getId(), dataUpdateTask);

        verify(taskRepository, times(1)).findById(taskModel.getId());

        assertThat(taskModel.getStatus()).isEqualTo(TaskStatus.DOIN);
    }

    @Test
    @DisplayName("Should throw a exception when don't found the task")
    void updateTaskStatusCase2() {
        UUID taskId = UUID.randomUUID();

        TaskStatusUpdateRequestDTO data = new TaskStatusUpdateRequestDTO(
                TaskStatus.TODO
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                TaskNotFoundException.class, () -> taskService.updateTaskStatus(taskId, data)
        );

        assertThat(exception.getMessage()).isEqualTo("Task not found");
    }
}