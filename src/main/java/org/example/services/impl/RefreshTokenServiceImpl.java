package org.example.services.impl;

import lombok.AllArgsConstructor;
import org.example.data.model.RefreshToken;
import org.example.data.repositories.RefreshTokenRepository;
import org.example.services.interfaces.RefreshTokenService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    @Override
    public RefreshToken generateRefreshToken(Long userId) {
        Instant now = Instant.now();
        Instant expiryTime = now.plus(1, ChronoUnit.DAYS);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setSupplierId(userId);
        refreshToken.setCreationTime(now);
        refreshToken.setExpirationTime(expiryTime);

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    @Override
    public void deleteAllRefreshToken() {
        refreshTokenRepository.deleteAll();
    }
}
