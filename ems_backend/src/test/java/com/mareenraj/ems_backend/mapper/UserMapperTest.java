package com.mareenraj.ems_backend.mapper;

import com.mareenraj.ems_backend.model.User;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


class UserMapperTest {
    EmployeeMapper employeeMapper;

    @BeforeEach
    void setUp() {
        employeeMapper = new EmployeeMapper();
    }

    @Test
    void mapToEmployeeDto() {
        User user = new User(777L, "Mareen", "raj", "j4D5H@example.com");
        EmployeeDto employeeDto = employeeMapper.mapToEmployeeDto(user);
        assertEquals(user.getId(), employeeDto.getId());
        assertEquals(user.getFirstName(), employeeDto.getFirstName());
        assertEquals(user.getLastName(), employeeDto.getLastName());
        assertEquals(user.getEmail(), employeeDto.getEmail());

    }

    @Test
    void mapToEmployee() {
        EmployeeDto employeeDto = new EmployeeDto(777L, "Mareen", "raj", "j4D5H@example.com");
        User user = employeeMapper.mapToEmployee(employeeDto);
        assertEquals(user.getId(), employeeDto.getId());
        assertEquals(user.getFirstName(), employeeDto.getFirstName());
        assertEquals(user.getLastName(), employeeDto.getLastName());
        assertEquals(user.getEmail(), employeeDto.getEmail());
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