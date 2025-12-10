package dev.matheuslf.desafio.inscritos.controller;

import dev.matheuslf.desafio.inscritos.models.dtos.DeleteUserRequest;
import dev.matheuslf.desafio.inscritos.models.dtos.UserResponseDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.UserUpdateRoleRequestDTO;
import dev.matheuslf.desafio.inscritos.models.entities.UserModel;
import dev.matheuslf.desafio.inscritos.models.repository.UserRepository;
import dev.matheuslf.desafio.inscritos.models.service.UserManagerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("manager-users")
public class UserManagerController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserManagerService userManagerService;

    @GetMapping("")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(Pageable pageable) {
        Page<UserResponseDTO> users = userManagerService.getAll(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserResponseDTO> getByEmail(@PathVariable("email") String email) {
        UserResponseDTO user = userManagerService.getByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update-role")
    public ResponseEntity<Void> updateUserRole(@Valid @RequestBody UserUpdateRoleRequestDTO data) {
        userManagerService.updateUserRole(data);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@Valid @RequestBody DeleteUserRequest data){
        userManagerService.deleteUser(data);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test")
    public long test(){
        UUID id = UUID.fromString("8a8af4d2-9886-4aca-8d76-b8eac06418b5");
        return userRepository.countOtherAdmins(id);
    }

}