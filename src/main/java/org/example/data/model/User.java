package org.example.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.config.validator.EmailValidatorObj;
import org.example.config.validator.NameValidatorObject;
import org.example.data.model.enums.AccountType;
import org.example.data.model.enums.Gender;
import org.example.data.model.enums.Role;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String email;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Role role;
    private String password;
    private LocalDateTime createdAt;
    private boolean isEnabled;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    @Column(unique = true)
    private String accountNumber;;


    public void validateUser(){
        EmailValidatorObj.validateEmail(email);
        NameValidatorObject.validateName(firstName);
        NameValidatorObject.validateName(lastName);
    }

}
