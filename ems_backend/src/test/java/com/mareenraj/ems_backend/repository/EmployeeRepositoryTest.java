package com.mareenraj.ems_backend.repository;

import com.mareenraj.ems_backend.model.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void employeeRepository_save_returnSavedEmployeeTest() {
        //Arrange
        Employee employee = Employee.builder()
                .firstName("Mareen")
                .lastName("Raj")
                .email("N6OJt@example.com")
                .build();

        //Act
        Employee savedEmployee = employeeRepository.save(employee);

        //Assert
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getId()).isGreaterThan(0L);
    }

    @Test
    public void employeeRepository_getAll_returnAllEmployeesTest() {
        //Arrange
        Employee employee1 = Employee.builder()
                .firstName("Mareen")
                .lastName("Raj")
                .email("N6OJt@example.com")
                .build();

        Employee employee2 = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("jdoe@me.com")
                .build();

        employeeRepository.save(employee1);
        employeeRepository.save(employee2);

        //Act
        List<Employee> employeeList = employeeRepository.findAll();

        //Assert
        assertThat(employeeList).isNotNull();
        assertThat(employeeList.size()).isEqualTo(2);
    }

    @Test
    public void employeeRepository_getById_returnEmployeeByIdTest() {

        //Arrange
        Employee employee = Employee.builder()
                .firstName("Chris")
                .lastName("Robin")
                .email("NkjnOJt@example.com")
                .build();
        employeeRepository.save(employee);

        //Act
        Employee foundEmployee = employeeRepository.findById(employee.getId()).get();

        //Assert
        assertThat(foundEmployee).isNotNull();
        assertThat(foundEmployee.getId()).isEqualTo(employee.getId());
    }

    @Test
    public void employeeRepository_getByEmail_returnEmployeeByEmailTest() {

        //Arrange
        Employee employee = Employee.builder()
                .firstName("Chris")
                .lastName("Robin")
                .email("NkjnOJt@example.com")
                .build();
        employeeRepository.save(employee);

        //Act
        Employee foundEmployee = employeeRepository.findByEmail(employee.getEmail()).get();

        //Assert
        assertThat(foundEmployee).isNotNull();
        assertThat(foundEmployee.getEmail()).isEqualTo(employee.getEmail());
    }

    @Test
    public void employeeRepository_deleteById_deleteEmployeeByIdTest() {
        //Arrange
        Employee employee = Employee.builder()
                .firstName("Chris")
                .lastName("Robin")
                .email("NkjnOJt@example.com")
                .build();
        Employee savedEmployee = employeeRepository.save(employee);

        //Act
        employeeRepository.deleteById(savedEmployee.getId());

        //Assert
        Optional<Employee> deletedEmployee = employeeRepository.findById(savedEmployee.getId());
        assertThat(deletedEmployee).isEmpty();
    }

    @Test
    public void employeeRepository_updateById_updateEmployeeByIdTest() {
        //Arrange
        Employee employee = Employee.builder()
                .firstName("Mahil")
                .lastName("Robin")
                .email("NkjnOmahilJt@example.com")
                .build();
        Employee savedEmployee = employeeRepository.save(employee);

        //Act
        savedEmployee.setId(savedEmployee.getId());
        savedEmployee.setFirstName("Mareen");
        savedEmployee.setLastName("Raj");
        savedEmployee.setEmail("N6OJt@example.com");
        Employee updatedEmployee = employeeRepository.save(savedEmployee);

        //Assert
        assertThat(updatedEmployee).isNotNull();
        assertThat(updatedEmployee.getFirstName()).isEqualTo("Mareen");
        assertThat(updatedEmployee.getLastName()).isEqualTo("Raj");
        assertThat(updatedEmployee.getEmail()).isEqualTo("N6OJt@example.com");
    }
}