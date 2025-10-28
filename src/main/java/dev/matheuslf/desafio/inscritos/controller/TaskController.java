package dev.matheuslf.desafio.inscritos.controller;

import dev.matheuslf.desafio.inscritos.models.dtos.TaskRequestDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.TaskResponseDTO;
import dev.matheuslf.desafio.inscritos.models.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping("/addTask")
    public void saveTask(@Valid @RequestBody TaskRequestDTO data){
        taskService.addTask(data);
    }

    @GetMapping("/")
    public List<TaskResponseDTO> getAll(){
        return taskService.getAll();
    }
}
