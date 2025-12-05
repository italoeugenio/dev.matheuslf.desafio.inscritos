package dev.matheuslf.desafio.inscritos.controller;

import dev.matheuslf.desafio.inscritos.models.dtos.DeleteUserRequest;
import dev.matheuslf.desafio.inscritos.models.dtos.UserResponseDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.UserUpdateRoleRequestDTO;
import dev.matheuslf.desafio.inscritos.models.service.UserManagerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("mananger-users")
public class UserManagerController {

    @Autowired
    private UserManagerService userManagerService;

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(Pageable pageable) {
        Page<UserResponseDTO> users = userManagerService.getAll(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserResponseDTO> getByEmail(@PathVariable("email") String email) {
        UserResponseDTO user = userManagerService.getByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/role/{email}/")
    public ResponseEntity<Void> updateUserRole(@PathVariable("email") String email, @RequestBody @Valid UserUpdateRoleRequestDTO data) {
        userManagerService.updateUserRole(data, email);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@RequestBody @Valid DeleteUserRequest data) {
        userManagerService.deleteUser(data);
        return ResponseEntity.noContent().build();
    }
}