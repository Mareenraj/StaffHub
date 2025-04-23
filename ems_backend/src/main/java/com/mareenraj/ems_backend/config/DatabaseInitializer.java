package com.mareenraj.ems_backend.config;

import com.mareenraj.ems_backend.model.Role;
import com.mareenraj.ems_backend.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseInitializer {
    @Bean
    public CommandLineRunner initDatabase(RoleRepository roleRepository) {
        return args -> {
            // Check if roles already exist
            if (roleRepository.findByName("ADMIN").isEmpty()) {
                Role adminRole = new Role();
                adminRole.setName("ADMIN");
                roleRepository.save(adminRole);
            }

            if (roleRepository.findByName("EMPLOYEE").isEmpty()) {
                Role employeeRole = new Role();
                employeeRole.setName("EMPLOYEE");
                roleRepository.save(employeeRole);
            }
        };
    }
}
