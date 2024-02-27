package com.mareenraj.ems_backend.mapper;

import com.mareenraj.ems_backend.dto.EmployeeDto;
import com.mareenraj.ems_backend.model.Employee;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


class EmployeeMapperTest {
    EmployeeMapper employeeMapper;

    @BeforeEach
    void setUp() {
        employeeMapper = new EmployeeMapper();
    }

    @Test
    void mapToEmployeeDto() {
        Employee employee = new Employee(777L, "Mareen", "raj", "j4D5H@example.com");
        EmployeeDto employeeDto = employeeMapper.mapToEmployeeDto(employee);
        assertEquals(employee.getId(), employeeDto.getId());
        assertEquals(employee.getFirstName(), employeeDto.getFirstName());
        assertEquals(employee.getLastName(), employeeDto.getLastName());
        assertEquals(employee.getEmail(), employeeDto.getEmail());

    }

    @Test
    void mapToEmployee() {
        EmployeeDto employeeDto = new EmployeeDto(777L, "Mareen", "raj", "j4D5H@example.com");
        Employee employee = employeeMapper.mapToEmployee(employeeDto);
        assertEquals(employee.getId(), employeeDto.getId());
        assertEquals(employee.getFirstName(), employeeDto.getFirstName());
        assertEquals(employee.getLastName(), employeeDto.getLastName());
        assertEquals(employee.getEmail(), employeeDto.getEmail());
    }

    @Test
    void shouldThrowExceptionIfEmployeeDtoIsNull() {
        var exp = Assertions.assertThrows(NullPointerException.class, () -> {
            employeeMapper.mapToEmployee(null);
        });
        assertEquals("EmployeeDto cannot be null", exp.getMessage());
    }

    @Test
    void shouldThrowExceptionIfEmployeeIsNull() {
        var exp = Assertions.assertThrows(NullPointerException.class, () -> {
            employeeMapper.mapToEmployeeDto(null);
        });
        assertEquals("Employee cannot be null", exp.getMessage());
    }
}