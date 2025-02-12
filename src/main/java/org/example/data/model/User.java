package org.example.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.data.model.enums.Role;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class User {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String email;
    private BigDecimal balance;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Role role;
    @Size(min = 8)
    private String password;
    private LocalDateTime createdAt;
    private boolean isEnabled;

}
