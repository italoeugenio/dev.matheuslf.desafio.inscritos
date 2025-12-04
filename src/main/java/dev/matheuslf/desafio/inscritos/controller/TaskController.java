package dev.matheuslf.desafio.inscritos.controller;

import dev.matheuslf.desafio.inscritos.config.swagger.docs.tasks.TaskApiDoc;
import dev.matheuslf.desafio.inscritos.enums.TaskPriority;
import dev.matheuslf.desafio.inscritos.enums.TaskStatus;
import dev.matheuslf.desafio.inscritos.models.dtos.*;
import dev.matheuslf.desafio.inscritos.models.entities.TaskModel;
import dev.matheuslf.desafio.inscritos.models.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("tasks")
@Tag(name = "Tasks", description = "Endpoints to manage the tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @TaskApiDoc.CreateTasks
    @PostMapping("/addTask")
    public ResponseEntity<Void> saveTask(@Valid @RequestBody TaskRequestDTO data) {
        TaskModel task = taskService.addTask(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @TaskApiDoc.GetAllTasks
    @GetMapping("/")
    public ResponseEntity<List<TaskResponseDetailsDTO>> getAll() {
        var tasks = taskService.getAll();
        return ResponseEntity.ok().body(tasks);
    }

    @TaskApiDoc.GetTaskById
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDetailsDTO> getById(@PathVariable("id") UUID id) {
        var task = taskService.getById(id);
        return ResponseEntity.ok().body(task);
    }

    @TaskApiDoc.DeleteTask
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @TaskApiDoc.GetTaskByParams
    @GetMapping("")
    public List<TaskResponseDetailsDTO> findTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) UUID projectId) {
        return taskService.findTasks(status,priority,projectId);
    }

    @TaskApiDoc.UpdateTask
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTask(@PathVariable("id") UUID id, @Valid @RequestBody TaskUpdateResquestDTO data) {
        taskService.updateTask(id, data);
        return ResponseEntity.ok().build();
    }

    @TaskApiDoc.UpdateTaskStatus
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateTaskStatus(@PathVariable("id") UUID id,@Valid @RequestBody TaskStatusUpdateDTO data) {
        taskService.updateTaskStatus(id, data);
        return ResponseEntity.ok().build();
    }

}
