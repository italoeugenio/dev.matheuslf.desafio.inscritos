package dev.matheuslf.desafio.inscritos.models.service;

import dev.matheuslf.desafio.inscritos.enums.TaskPriority;
import dev.matheuslf.desafio.inscritos.enums.TaskStatus;
import dev.matheuslf.desafio.inscritos.exceptions.project.ProjectNotFoundException;
import dev.matheuslf.desafio.inscritos.exceptions.task.TaskException;
import dev.matheuslf.desafio.inscritos.exceptions.task.TaskNotFoundException;
import dev.matheuslf.desafio.inscritos.models.dtos.TaskRequestDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.TaskResponseDetailsDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.TaskStatusUpdateDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.TaskUpdateResquestDTO;
import dev.matheuslf.desafio.inscritos.models.entities.ProjectModel;
import dev.matheuslf.desafio.inscritos.models.entities.TaskModel;
import dev.matheuslf.desafio.inscritos.models.repository.ProjectRepository;
import dev.matheuslf.desafio.inscritos.models.repository.TaskRepository;
import dev.matheuslf.desafio.inscritos.specification.TaskSpecification;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public TaskModel addTask(TaskRequestDTO data) {
        ProjectModel project = projectRepository.findById(data.projectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
        if (data.dueDate().isAfter(project.getEndDate()))
            throw new TaskException("The due date of the task can't be after the end date of the project");
        TaskModel task = new TaskModel(data);
        task.setProject(project);
        return taskRepository.save(task);
    }


    public List<TaskResponseDetailsDTO> getAll() {
        List<TaskResponseDetailsDTO> tasks = taskRepository.findAll().stream()
                .map(task -> new TaskResponseDetailsDTO(task))
                .toList();
        return tasks;
    }

    public TaskResponseDetailsDTO getById(UUID id) {
        TaskModel taskModel = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        return new TaskResponseDetailsDTO(taskModel);
    }

    public void deleteTask(UUID id) {
        TaskModel taskModel = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        taskRepository.deleteById(id);
    }

    public List<TaskResponseDetailsDTO> findTasks(TaskStatus status, TaskPriority priority, UUID projectId) {
        Specification<TaskModel> spec = Specification.where(null);

        if (status != null) spec = spec.and(TaskSpecification.hasStatus(status));
        if (priority != null) spec = spec.and(TaskSpecification.hasPriority(priority));
        if (projectId != null) spec = spec.and(TaskSpecification.hasProjectId(projectId));

        return taskRepository.findAll(spec).stream()
                .map(TaskResponseDetailsDTO::new)
                .toList();
    }

    public void updateTask(UUID id, TaskUpdateResquestDTO data) {
        TaskModel task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        if (data.dueDate().isAfter(task.getProject().getEndDate()))
            throw new TaskException("The due date of the task can't be after the end date of the project");
        BeanUtils.copyProperties(data, task);
        taskRepository.save(task);
    }

    public void updateTaskStatus(UUID id, TaskStatusUpdateDTO data) {
        TaskModel task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        BeanUtils.copyProperties(data, task);
        taskRepository.save(task);
    }
}
