package com.mareenraj.ems_backend.service;

import com.mareenraj.ems_backend.dto.EmployeeDto;
import com.mareenraj.ems_backend.mapper.EmployeeMapper;
import com.mareenraj.ems_backend.model.Employee;
import com.mareenraj.ems_backend.repository.EmployeeRepository;
import com.mareenraj.ems_backend.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {
    @InjectMocks
    private EmployeeServiceImpl employeeService;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmployeeMapper employeeMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createEmployee() {
        //Arrange
        EmployeeDto employeeDto = new EmployeeDto(345L, "John", "Doe", "jdoe@me.com");
        Employee employee = new Employee(345L, "John", "Doe", "jdoe@me.com");
        Employee savedEmployee = new Employee(345L, "John", "Doe", "jdoe@me.com");

        //Mock the calls
        when(employeeMapper.mapToEmployee(employeeDto)).thenReturn(employee);
        when(employeeRepository.save(employee)).thenReturn(savedEmployee);
        when(employeeMapper.mapToEmployeeDto(savedEmployee)).thenReturn(employeeDto);

        //Act
        EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto);

        //Assert
        assertEquals(employeeDto, createdEmployee);

        verify(employeeMapper).mapToEmployee(employeeDto);
        verify(employeeRepository).save(employee);
        verify(employeeMapper).mapToEmployeeDto(savedEmployee);
    }

    @Test
    void getEmployeeById() {
        //Arrange
        Long employeeId = 345L;
        Employee employee = new Employee(
                345L,
                "John",
                "Doe",
                "jdoe@me.com"
        );

        //Mock the calls
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeMapper.mapToEmployeeDto(any(Employee.class))).thenReturn(new EmployeeDto(
                345L, "John", "Doe", "jdoe@me.com"
        ));

        //Act
        EmployeeDto employeeDto = employeeService.getEmployeeById(employeeId);

        //Assert
        assertEquals(employee.getFirstName(), employeeDto.getFirstName());
        assertEquals(employee.getLastName(), employeeDto.getLastName());
        assertEquals(employee.getEmail(), employeeDto.getEmail());

        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    void getAllEmployees() {
        //Arrange
        List<Employee> employees = Collections.singletonList(new Employee(345L, "John", "Doe", "jdoe@me.com"));

        //Mock the calls
        when(employeeRepository.findAll()).thenReturn(employees);
        when(employeeMapper.mapToEmployeeDto(any(Employee.class))).thenReturn(new EmployeeDto(345L, "John", "Doe", "jdoe@me.com"));

        //Act
        List<EmployeeDto> employeeDtos = employeeService.getAllEmployees();

        //Assert
        assertEquals(employees.stream().map(employeeMapper::mapToEmployeeDto).collect(Collectors.toList()), employeeDtos);

        verify(employeeRepository).findAll();
    }

    @Test
    public void testUpdateEmployee() {
        // Arrange
        Long employeeId = 345L;

        // Create a separate DTO for expected values to avoid comparing references
        EmployeeDto expectedUpdatedEmployeeDto = new EmployeeDto(employeeId, "John", "Doe", "jdoe@me.com");
        Employee existingEmployee = new Employee(employeeId, "Jane", "Doe", "janedoe@me.com");
        Employee updatedEmployee = new Employee(employeeId, "John", "Doe", "jdoe@me.com");
        EmployeeDto UpdatedEmployeeDto = new EmployeeDto(employeeId, "John", "Doe", "jdoe@me.com");

        // Mock repository behavior
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);
        when(employeeMapper.mapToEmployeeDto(updatedEmployee)).thenReturn(UpdatedEmployeeDto);

        // Act
        EmployeeDto returnedEmployeeDto = employeeService.updateEmployee(employeeId, expectedUpdatedEmployeeDto);

        // Assert
        assertNotNull(returnedEmployeeDto);
        assertEquals(expectedUpdatedEmployeeDto.getFirstName(), returnedEmployeeDto.getFirstName());
        assertEquals(expectedUpdatedEmployeeDto.getLastName(), returnedEmployeeDto.getLastName());
        assertEquals(expectedUpdatedEmployeeDto.getEmail(), returnedEmployeeDto.getEmail());

        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).save(updatedEmployee);
    }

    @Test
    public void testDeleteEmployee() {
        //Arrange
        Long employeeId = 456L;
        Employee employee = new Employee();

        // Mock repository behavior
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        //Act
        employeeService.deleteEmployee(employeeId);

        // then
        // No assertions needed as the method doesn't return a value

        //Verify that the repository methods were called with the correct parameters
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).deleteById(employeeId);
    }

    @Test
    public void testGetEmployeeByEmail() {
        // Arrange
        String email = "johndoe@me.com";
        Employee employee = new Employee(1L, "John", "Doe", email);
        EmployeeDto employeeDto = new EmployeeDto(1L, "John", "Doe", email);

        // Mock the calls
        when(employeeRepository.findByEmail(email)).thenReturn(Optional.of(employee));
        when(employeeMapper.mapToEmployeeDto(employee)).thenReturn(employeeDto);

        // Act
        EmployeeDto returnedEmployeeDto = employeeService.getEmployeeByEmail(email);

        // Assert
        assertNotNull(returnedEmployeeDto); // Ensure that the returnedEmployeeDto is not null
        assertEquals(employee.getId(), returnedEmployeeDto.getId());
        assertEquals(employee.getFirstName(), returnedEmployeeDto.getFirstName());
        assertEquals(employee.getLastName(), returnedEmployeeDto.getLastName());
        assertEquals(employee.getEmail(), returnedEmployeeDto.getEmail());

        verify(employeeRepository).findByEmail(email);
    }
}