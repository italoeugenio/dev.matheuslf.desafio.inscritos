package dev.matheuslf.desafio.inscritos.models.service;

import dev.matheuslf.desafio.inscritos.exceptions.User.UserNotFound;
import dev.matheuslf.desafio.inscritos.models.dtos.DeleteUserRequest;
import dev.matheuslf.desafio.inscritos.models.dtos.EmailMessageDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.UserResponseDTO;
import dev.matheuslf.desafio.inscritos.models.dtos.UserUpdateRoleRequestDTO;
import dev.matheuslf.desafio.inscritos.models.entities.UserModel;
import dev.matheuslf.desafio.inscritos.models.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserManagerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    public Page<UserResponseDTO> getAll(Pageable pageable){
        Page<UserModel> users = userRepository.findAll(pageable);
        return  users.map(user -> new UserResponseDTO(user));
    }

    public UserResponseDTO getByEmail(String email){
        var user = userRepository.findUserModelByEmail(email);
        if(user == null) throw new UserNotFound("User not found");
        return new UserResponseDTO(user);
    }

    public void updateUserRole(UserUpdateRoleRequestDTO data, String email){
        var user = userRepository.findUserModelByEmail(email);
        if(user == null) throw new UserNotFound("User not found");
        BeanUtils.copyProperties(data, user);
        return;
    }

    public void deleteUser(DeleteUserRequest data){
        var user = userRepository.findUserModelByEmail(data.email());
        if(user == null) throw new UserNotFound("User not found");
        EmailMessageDTO emailMessageDTO = new EmailMessageDTO(data.email(), "DELETED ACCOUNT", data.deleteMessage());
        emailSenderService.sendEmailVerificationCode(emailMessageDTO);
        userRepository.delete(user);
    }
}
