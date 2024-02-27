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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        EmployeeDto employeeDto = new EmployeeDto(345L,"John", "Doe", "jdoe@me.com");
        Employee employee = new Employee(345L,"John", "Doe", "jdoe@me.com");
        Employee savedEmployee = new Employee(345L,"John", "Doe", "jdoe@me.com");

        //Mock the calls
        Mockito.when(employeeMapper.mapToEmployee(employeeDto)).thenReturn(employee);
        Mockito.when(employeeRepository.save(employee)).thenReturn(savedEmployee);
        Mockito.when(employeeMapper.mapToEmployeeDto(savedEmployee)).thenReturn(new EmployeeDto(345L,"John", "Doe", "jdoe@me.com"));

        //when
        EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto);

        //then
        assertEquals(employeeDto.getFirstName(), createdEmployee.getFirstName());
        assertEquals(employeeDto.getLastName(), createdEmployee.getLastName());
        assertEquals(employeeDto.getEmail(), createdEmployee.getEmail());

        verify(employeeMapper,times(1)).mapToEmployee(employeeDto);
        verify(employeeRepository,times(1)).save(employee);
        verify(employeeMapper,times(1)).mapToEmployeeDto(savedEmployee);
    }

    @Test
    void getEmployeeById() {
    }

    @Test
    void getAllEmployees() {
    }

    @Test
    void updateEmployee() {
    }

    @Test
    void deleteEmployee() {
    }
}