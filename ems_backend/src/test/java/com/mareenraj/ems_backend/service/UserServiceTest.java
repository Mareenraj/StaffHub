package com.mareenraj.ems_backend.service;

import com.mareenraj.ems_backend.model.User;
import com.mareenraj.ems_backend.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private EmployeeServiceImpl employeeService;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmployeeMapper employeeMapper;

    @Test
    void createEmployee() {
        //Arrange
        EmployeeDto employeeDto = new EmployeeDto(345L, "John", "Doe", "jdoe@me.com");
        User user = new User(345L, "John", "Doe", "jdoe@me.com");
        User savedUser = new User(345L, "John", "Doe", "jdoe@me.com");

        //Mock the calls
        when(employeeMapper.mapToEmployee(employeeDto)).thenReturn(user);
        when(employeeRepository.save(user)).thenReturn(savedUser);
        when(employeeMapper.mapToEmployeeDto(savedUser)).thenReturn(employeeDto);

        //Act
        EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto);

        //Assert
        assertEquals(employeeDto, createdEmployee);

        verify(employeeMapper).mapToEmployee(employeeDto);
        verify(employeeRepository).save(user);
        verify(employeeMapper).mapToEmployeeDto(savedUser);
    }

    @Test
    void getEmployeeById() {
        //Arrange
        Long employeeId = 345L;
        User user = new User(
                345L,
                "John",
                "Doe",
                "jdoe@me.com"
        );

        //Mock the calls
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(user));
        when(employeeMapper.mapToEmployeeDto(any(User.class))).thenReturn(new EmployeeDto(
                345L, "John", "Doe", "jdoe@me.com"
        ));

        //Act
        EmployeeDto employeeDto = employeeService.getEmployeeById(employeeId);

        //Assert
        assertEquals(user.getFirstName(), employeeDto.getFirstName());
        assertEquals(user.getLastName(), employeeDto.getLastName());
        assertEquals(user.getEmail(), employeeDto.getEmail());

        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    void getAllEmployees() {
        //Arrange
        List<User> users = Collections.singletonList(new User(345L, "John", "Doe", "jdoe@me.com"));

        //Mock the calls
        when(employeeRepository.findAll()).thenReturn(users);
        when(employeeMapper.mapToEmployeeDto(any(User.class))).thenReturn(new EmployeeDto(345L, "John", "Doe", "jdoe@me.com"));

        //Act
        List<EmployeeDto> employeeDtos = employeeService.getAllEmployees();

        //Assert
        assertEquals(users.stream().map(employeeMapper::mapToEmployeeDto).collect(Collectors.toList()), employeeDtos);

        verify(employeeRepository).findAll();
    }

    @Test
    public void testUpdateEmployee() {
        // Arrange
        Long employeeId = 345L;

        // Create a separate DTO for expected values to avoid comparing references
        EmployeeDto expectedUpdatedEmployeeDto = new EmployeeDto(employeeId, "John", "Doe", "jdoe@me.com");
        User existingUser = new User(employeeId, "Jane", "Doe", "janedoe@me.com");
        User updatedUser = new User(employeeId, "John", "Doe", "jdoe@me.com");
        EmployeeDto UpdatedEmployeeDto = new EmployeeDto(employeeId, "John", "Doe", "jdoe@me.com");

        // Mock repository behavior
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingUser));
        when(employeeRepository.save(any(User.class))).thenReturn(updatedUser);
        when(employeeMapper.mapToEmployeeDto(updatedUser)).thenReturn(UpdatedEmployeeDto);

        // Act
        EmployeeDto returnedEmployeeDto = employeeService.updateEmployee(employeeId, expectedUpdatedEmployeeDto);

        // Assert
        assertNotNull(returnedEmployeeDto);
        assertEquals(expectedUpdatedEmployeeDto.getFirstName(), returnedEmployeeDto.getFirstName());
        assertEquals(expectedUpdatedEmployeeDto.getLastName(), returnedEmployeeDto.getLastName());
        assertEquals(expectedUpdatedEmployeeDto.getEmail(), returnedEmployeeDto.getEmail());

        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).save(updatedUser);
    }

    @Test
    public void testDeleteEmployee() {
        //Arrange
        Long employeeId = 456L;
        User user = new User();

        // Mock repository behavior
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(user));

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
        User user = new User(1L, "John", "Doe", email);
        EmployeeDto employeeDto = new EmployeeDto(1L, "John", "Doe", email);

        // Mock the calls
        when(employeeRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(employeeMapper.mapToEmployeeDto(user)).thenReturn(employeeDto);

        // Act
        EmployeeDto returnedEmployeeDto = employeeService.getEmployeeByEmail(email);

        // Assert
        assertNotNull(returnedEmployeeDto); // Ensure that the returnedEmployeeDto is not null
        assertEquals(user.getId(), returnedEmployeeDto.getId());
        assertEquals(user.getFirstName(), returnedEmployeeDto.getFirstName());
        assertEquals(user.getLastName(), returnedEmployeeDto.getLastName());
        assertEquals(user.getEmail(), returnedEmployeeDto.getEmail());

        verify(employeeRepository).findByEmail(email);
    }
}