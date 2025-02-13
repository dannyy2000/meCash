package org.example.data.repositories;

import org.example.data.model.AuthOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthOtpRepository extends JpaRepository<AuthOtp,Long> {

    Optional<AuthOtp> findByOtpValue(String otpValue);
}
