package com.mareenraj.ems_backend.controller;

import com.mareenraj.ems_backend.dto.JwtResponse;
import com.mareenraj.ems_backend.dto.LoginRequest;
import com.mareenraj.ems_backend.dto.MessageResponse;
import com.mareenraj.ems_backend.dto.SignupRequest;
import com.mareenraj.ems_backend.model.RefreshToken;
import com.mareenraj.ems_backend.model.Role;
import com.mareenraj.ems_backend.model.User;
import com.mareenraj.ems_backend.repository.RoleRepository;
import com.mareenraj.ems_backend.repository.UserRepository;
import com.mareenraj.ems_backend.security.jwt.JwtUtils;
import com.mareenraj.ems_backend.security.service.RefreshTokenService;
import com.mareenraj.ems_backend.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.generateAccessToken(loginRequest.getUsername());

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
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

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}