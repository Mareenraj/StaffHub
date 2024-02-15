package com.mareenraj.ems_backend.repository;

import com.mareenraj.ems_backend.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
