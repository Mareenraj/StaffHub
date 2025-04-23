package com.mareenraj.ems_backend.repository;

import com.mareenraj.ems_backend.model.RefreshToken;
import com.mareenraj.ems_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByUser(User user);
}
