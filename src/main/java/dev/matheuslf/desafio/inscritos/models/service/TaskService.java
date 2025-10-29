package dev.matheuslf.desafio.inscritos.models.service;

import dev.matheuslf.desafio.inscritos.exceptions.project.ProjectNotFoundException;
import dev.matheuslf.desafio.inscritos.models.dtos.TaskRequestDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.TaskResponseDTO;
import dev.matheuslf.desafio.inscritos.models.entities.ProjectModel;
import dev.matheuslf.desafio.inscritos.models.entities.TaskModel;
import dev.matheuslf.desafio.inscritos.models.repository.ProjectRepository;
import dev.matheuslf.desafio.inscritos.models.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public void addTask(TaskRequestDTO data) {
        ProjectModel project = projectRepository.findById(data.projectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project don't found"));
        TaskModel task = new TaskModel(data);
        task.setProject(project);
        taskRepository.save(task);
    }


    public List<TaskResponseDTO> getAll(){
        List<TaskResponseDTO> tasks = taskRepository.findAll().stream()
                .map(task -> new TaskResponseDTO(task))
                .toList();
        return tasks;
    }
}
