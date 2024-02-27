package com.mareenraj.ems_backend.mapper;

import com.mareenraj.ems_backend.dto.EmployeeDto;
import com.mareenraj.ems_backend.model.Employee;

public class EmployeeMapper {
    public static EmployeeDto mapToEmployeeDto(Employee employee){
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

    public static Employee mapToEmployee(EmployeeDto employeeDto){
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
