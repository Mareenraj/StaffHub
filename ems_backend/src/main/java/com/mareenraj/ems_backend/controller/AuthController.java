package com.mareenraj.ems_backend.controller;

import com.mareenraj.ems_backend.dto.*;
import com.mareenraj.ems_backend.model.EmailVerificationToken;
import com.mareenraj.ems_backend.model.RefreshToken;
import com.mareenraj.ems_backend.model.Role;
import com.mareenraj.ems_backend.model.User;
import com.mareenraj.ems_backend.repository.EmailVerificationTokenRepository;
import com.mareenraj.ems_backend.repository.RoleRepository;
import com.mareenraj.ems_backend.repository.UserRepository;
import com.mareenraj.ems_backend.security.jwt.JwtUtils;
import com.mareenraj.ems_backend.security.service.EmailService;
import com.mareenraj.ems_backend.security.service.RefreshTokenService;
import com.mareenraj.ems_backend.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@EnableAsync
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils, RefreshTokenService refreshTokenService, EmailVerificationTokenRepository emailVerificationTokenRepository, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.emailService = emailService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUserName(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User();
        user.setUserName(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setEnabled(false);

        Set<String> strRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            // Default role is EMPLOYEE
            Role employeeRole = roleRepository.findByName("EMPLOYEE")
                    .orElseThrow(() -> new RuntimeException("Error: Role EMPLOYEE is not found."));
            roles.add(employeeRole);
        } else {
            strRoles.forEach(role -> {
                if (role.equals("admin")) {
                    Role adminRole = roleRepository.findByName("ADMIN")
                            .orElseThrow(() -> new RuntimeException("Error: Role ADMIN is not found."));
                    roles.add(adminRole);
                } else {
                    Role employeeRole = roleRepository.findByName("EMPLOYEE")
                            .orElseThrow(() -> new RuntimeException("Error: Role EMPLOYEE is not found."));
                    roles.add(employeeRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        try {
            String verificationToken = generateVerificationToken(user);
            String verificationUrl = "http://staffhub/api/auth/verify?token=" + verificationToken;
            emailService.sendVerificationEmail(user, verificationUrl);
        } catch (Exception e) {
            userRepository.delete(user);
            return ResponseEntity
                    .internalServerError()
                    .body(new MessageResponse("Error: Could not send verification email"));
        }

        return ResponseEntity.ok(
                new MessageResponse("User registered successfully! Please check your email for verification instructions")
        );
    }

    @Scheduled(cron = "0 0 0 * * ?") // Daily at midnight
    public void purgeExpiredTokens() {
        Date now = new Date();
        emailVerificationTokenRepository.deleteAllByExpiryDateBefore(now);
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(calculateExpiryDate());
        emailVerificationTokenRepository.save(verificationToken);
        return token;
    }

    private Date calculateExpiryDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 24); // 24 hours expiration
        return cal.getTime();
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestParam String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Invalid verification token"));
        }

        if (new Date().after(verificationToken.getExpiryDate())) {
            emailVerificationTokenRepository.delete(verificationToken);
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Verification token has expired"));
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        emailVerificationTokenRepository.delete(verificationToken);

        return ResponseEntity.ok(new MessageResponse("Email verified successfully! You can now log in"));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerificationEmail(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Error: User not found with email: " + email));

        if (user.isEnabled()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Account is already verified"));
        }

        // Delete existing tokens
        emailVerificationTokenRepository.deleteByUser(user);

        // Generate new token
        String verificationToken = generateVerificationToken(user);
        String verificationUrl = "http://staffhub/api/auth/verify?token=" + verificationToken;
        emailService.sendVerificationEmail(user, verificationUrl);

        return ResponseEntity.ok(new MessageResponse("Verification email resent successfully"));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            if (!userDetails.isEnabled()) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Account not verified - Please check your email"));
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = jwtUtils.generateAccessToken(loginRequest.getUsername());

            RefreshToken refreshToken =
                    refreshTokenService.createRefreshToken(userDetails.getUsername());

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(granted -> granted.getAuthority().substring(5)) // drop "ROLE_"
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    new JwtResponse(
                            accessToken,
                            refreshToken.getToken(),
                            userDetails.getId(),
                            userDetails.getUsername(),
                            userDetails.getEmail(),
                            roles
                    )
            );

        } catch (DisabledException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Account not verified - Please check your email"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Invalid credentials"));
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody LogoutRequest logoutRequest) {
        try {
            refreshTokenService.deleteByToken(logoutRequest.getRefreshToken());
            return ResponseEntity.ok(new MessageResponse("Logout successful"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Invalid refresh token"));
        }
    }
}