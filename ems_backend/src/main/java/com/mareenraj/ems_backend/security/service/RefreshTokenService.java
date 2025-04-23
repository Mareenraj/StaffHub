package com.mareenraj.ems_backend.security.service;

import com.mareenraj.ems_backend.model.RefreshToken;
import com.mareenraj.ems_backend.model.User;
import com.mareenraj.ems_backend.repository.RefreshTokenRepository;
import com.mareenraj.ems_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${app.jwt-refresh-expiration-ms}")
    private long refreshDurationMs;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow();
        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setExpiryDate(new Date(System.currentTimeMillis() + refreshDurationMs));
        rt.setToken(UUID.randomUUID().toString());
        return refreshTokenRepository.save(rt);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().before(new Date())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }
        return token;
    }

    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
