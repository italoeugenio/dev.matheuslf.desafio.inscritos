package dev.matheuslf.desafio.inscritos.models.entities;

import dev.matheuslf.desafio.inscritos.enums.CodeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "tb_validation_codes")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class ValidationCodesModel {
    private static final long serialVersion = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "validation_code_id")
    private UUID id;

    @Column(name = "code", nullable = false)
    private String code;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private UserModel user;

    @Enumerated(EnumType.STRING)
    @Column(name = "code_type", nullable = false)
    private CodeType codeType;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "create_at")
    private LocalDateTime createAt = LocalDateTime.now();

}
