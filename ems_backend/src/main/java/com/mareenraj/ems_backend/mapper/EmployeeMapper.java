package com.mareenraj.ems_backend.mapper;

import com.mareenraj.ems_backend.dto.EmployeeDto;
import com.mareenraj.ems_backend.model.Employee;
import org.springframework.stereotype.Service;

@Service
public class EmployeeMapper {
    public EmployeeDto mapToEmployeeDto(Employee employee){
        if (employee == null) {
            throw new NullPointerException("Employee cannot be null");
        }
        return new EmployeeDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail()
        );
    }

    public Employee mapToEmployee(EmployeeDto employeeDto){
        if (employeeDto == null) {
            throw new NullPointerException("EmployeeDto cannot be null");
        }
        return new Employee(
                employeeDto.getId(),
                employeeDto.getFirstName(),
                employeeDto.getLastName(),
                employeeDto.getEmail()
        );
    }
}
