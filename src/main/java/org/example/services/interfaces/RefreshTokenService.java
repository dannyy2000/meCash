package org.example.services.interfaces;

import org.example.data.model.RefreshToken;

public interface RefreshTokenService {
    RefreshToken generateRefreshToken(Long userId);

    void deleteRefreshToken(String token);

    void deleteAllRefreshToken();
}
