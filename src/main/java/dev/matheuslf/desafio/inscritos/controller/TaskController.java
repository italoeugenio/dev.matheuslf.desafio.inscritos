package dev.matheuslf.desafio.inscritos.controller;

import dev.matheuslf.desafio.inscritos.enums.TaskPriority;
import dev.matheuslf.desafio.inscritos.enums.TaskStatus;
import dev.matheuslf.desafio.inscritos.models.dtos.*;
import dev.matheuslf.desafio.inscritos.models.entities.TaskModel;
import dev.matheuslf.desafio.inscritos.models.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping("/addTask")
    public ResponseEntity saveTask(@Valid @RequestBody TaskRequestDTO data) {
        TaskModel task = taskService.addTask(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/")
    public List<TaskResponseDetailsDTO> getAll() {
        return taskService.getAll();
    }

    @GetMapping("/{id}")
    public TaskResponseDetailsDTO getById(@PathVariable("id") UUID id) {
        return taskService.getById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteTask(@PathVariable("id") UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("")
    public List<TaskResponseDetailsDTO> findTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) UUID projectId) {
        return taskService.findTasks(status,priority,projectId);
    }

    @PutMapping("/{id}")
    public void updateTask(@PathVariable("id") UUID id, @RequestBody TaskUpdateResquestDTO data) {
        taskService.updateTask(id, data);
    }

    @PutMapping("/{id}/status")
    public void updateTaskStatus(@PathVariable("id") UUID id, @RequestBody TaskStatusUpdateDTO data) {
        taskService.updateTaskStatus(id, data);
    }

}
