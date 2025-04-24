package com.mareenraj.ems_backend.repository;

import com.mareenraj.ems_backend.model.EmailVerificationToken;
import com.mareenraj.ems_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    EmailVerificationToken findByToken(String token);

    void deleteByUser(User user);

    void deleteAllByExpiryDateBefore(Date now);
}
