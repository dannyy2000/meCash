package org.example.data.repositories;

import org.example.data.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    void deleteByToken(String token);
}
