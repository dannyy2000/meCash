package org.example.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,generator = "token_id")

    private Long id;
    @Column(unique = true)
    private String otpValue;
    private LocalDateTime creationTime = LocalDateTime.now();
    private LocalDateTime expiryTime;
    private boolean used;
    public boolean isExpired(){
        return creationTime.isAfter(expiryTime);
    }
}
