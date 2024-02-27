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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        //given
        EmployeeDto employeeDto = new EmployeeDto(345L, "John", "Doe", "jdoe@me.com");
        Employee employee = new Employee(345L, "John", "Doe", "jdoe@me.com");
        Employee savedEmployee = new Employee(345L, "John", "Doe", "jdoe@me.com");

        //Mock the calls
        Mockito.when(employeeMapper.mapToEmployee(employeeDto)).thenReturn(employee);
        Mockito.when(employeeRepository.save(employee)).thenReturn(savedEmployee);
        Mockito.when(employeeMapper.mapToEmployeeDto(savedEmployee)).thenReturn(new EmployeeDto(345L, "John", "Doe", "jdoe@me.com"));

        //when
        EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto);

        //then
        assertEquals(employeeDto.getFirstName(), createdEmployee.getFirstName());
        assertEquals(employeeDto.getLastName(), createdEmployee.getLastName());
        assertEquals(employeeDto.getEmail(), createdEmployee.getEmail());

        verify(employeeMapper, times(1)).mapToEmployee(employeeDto);
        verify(employeeRepository, times(1)).save(employee);
        verify(employeeMapper, times(1)).mapToEmployeeDto(savedEmployee);
    }

    @Test
    void getEmployeeById() {
        //given
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

        //when
        EmployeeDto employeeDto = employeeService.getEmployeeById(employeeId);

        //then
        assertEquals(employee.getFirstName(), employeeDto.getFirstName());
        assertEquals(employee.getLastName(), employeeDto.getLastName());
        assertEquals(employee.getEmail(),employeeDto.getEmail());

        verify(employeeRepository,times(1)).findById(employeeId);
    }

    @Test
    void getAllEmployees() {
        //given
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(345L, "John", "Doe", "jdoe@me.com"));

        //Mock the calls
        when(employeeRepository.findAll()).thenReturn(employees);
        when(employeeMapper.mapToEmployeeDto(any(Employee.class))).thenReturn(new EmployeeDto(
                345L, "John", "Doe", "jdoe@me.com"
        ));

        //when
        List<EmployeeDto> employeeDtos = employeeService.getAllEmployees();

        //then
        assertEquals(employees.size(), employeeDtos.size());

        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateEmployee() {
        // Given
        Long employeeId = 345L;
        EmployeeDto updatedEmployeeDto = new EmployeeDto(employeeId, "John", "Doe", "jdoe@me.com");
        Employee existingEmployee = new Employee(employeeId, "Jane", "Doe", "janedoe@me.com");

        // Mock repository behavior
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(employeeMapper.mapToEmployeeDto(any(Employee.class))).thenAnswer(invocation -> {
            Employee employee = invocation.getArgument(0);
            return new EmployeeDto(employee.getId(), employee.getFirstName(), employee.getLastName(), employee.getEmail());
        });

        // When
        EmployeeDto updatedEmployee = employeeService.updateEmployee(employeeId, updatedEmployeeDto);

        // Then
        assertEquals(updatedEmployeeDto.getFirstName(), updatedEmployee.getFirstName());
        assertEquals(updatedEmployeeDto.getLastName(), updatedEmployee.getLastName());
        assertEquals(updatedEmployeeDto.getEmail(), updatedEmployee.getEmail());

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(employeeMapper, times(1)).mapToEmployeeDto(any(Employee.class));
    }

    @Test
    public void testDeleteEmployee() {
        // given
        Long employeeId = 456L;

        // mock calls
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(new Employee()));

        // when
        employeeService.deleteEmployee(employeeId);

        // then
        // No assertions needed as the method doesn't return a value

        // verify interactions
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, times(1)).deleteById(employeeId);
    }
}