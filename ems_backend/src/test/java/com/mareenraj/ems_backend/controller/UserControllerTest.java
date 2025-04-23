package com.mareenraj.ems_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmployeeController.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;


    @Test
    void createEmployee() throws Exception {
        // Arrange
        EmployeeDto employeeDto = new EmployeeDto(2L, "John", "Doe", "wvqJ3@example.com");

        when(employeeService.createEmployee(any(EmployeeDto.class))).thenReturn(employeeDto);

        mockMvc.perform(post("/api/employee/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("wvqJ3@example.com"));
    }

    @Test
    void getEmployeeById() throws Exception {
        Long employeeId = 1L;
        EmployeeDto employeeDto = new EmployeeDto(employeeId, "John", "Doe", "wvqJ3@example.com");

        when(employeeService.getEmployeeById(employeeId)).thenReturn(employeeDto);

        mockMvc.perform(get("/api/employee/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employeeId))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("wvqJ3@example.com"));

        verify(employeeService).getEmployeeById(employeeId);
    }

    @Test
    void getAllEmployees() throws Exception {
        List<EmployeeDto> employeeDtos = Arrays.asList(
                new EmployeeDto(1L, "John", "Doe", "john3@example.com"),
                new EmployeeDto(2L, "Jane", "Doe", "jane3@example.com")
        );

        when(employeeService.getAllEmployees()).thenReturn(employeeDtos);

        mockMvc.perform(get("/api/employee/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].email").value("john3@example.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].email").value("jane3@example.com"));

        verify(employeeService).getAllEmployees();
    }

    @Test
    void updateEmployee() throws Exception {
        Long employeeId = 1L;
        EmployeeDto updatedEmployeeDto = new EmployeeDto(employeeId, "John", "Doe", "wvqJ3@example.com");

        when(employeeService.updateEmployee(eq(employeeId), any(EmployeeDto.class))).thenReturn(updatedEmployeeDto);

        mockMvc.perform(
                        put("/api/employee/{id}", employeeId)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(updatedEmployeeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("wvqJ3@example.com"));

        verify(employeeService).updateEmployee(eq(employeeId), any(EmployeeDto.class));
    }

    @Test
    void deleteEmployee() throws Exception {
        Long employeeId = 1L;

        doNothing().when(employeeService).deleteEmployee(employeeId);

        mockMvc.perform(delete("/api/employee/{id}",employeeId))
                .andExpect(status().isOk());

        verify(employeeService, times(1)).deleteEmployee(employeeId);

    }

    @Test
    void getEmployeeByEmail() throws Exception {
        String employeeEmail = "wvqJ3@example.com";
        EmployeeDto employeeDto = new EmployeeDto(1L, "John", "Doe", employeeEmail);

        when(employeeService.getEmployeeByEmail(employeeEmail)).thenReturn(employeeDto);

        mockMvc.perform(get("/api/employee/email/{email}", employeeEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value(employeeEmail));

        verify(employeeService, times(1)).getEmployeeByEmail(employeeEmail);
    }
}