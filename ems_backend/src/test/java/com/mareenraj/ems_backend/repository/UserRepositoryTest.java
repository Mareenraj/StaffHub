package com.mareenraj.ems_backend.repository;

import com.mareenraj.ems_backend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void employeeRepository_save_returnSavedEmployeeTest() {
        //Arrange
        User user = User.builder()
                .firstName("Mareen")
                .lastName("Raj")
                .email("N6OJt@example.com")
                .build();

        //Act
        User savedUser = employeeRepository.save(user);

        //Assert
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isGreaterThan(0L);
    }

    @Test
    public void employeeRepository_getAll_returnAllEmployeesTest() {
        //Arrange
        User user1 = User.builder()
                .firstName("Mareen")
                .lastName("Raj")
                .email("N6OJt@example.com")
                .build();

        User user2 = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("jdoe@me.com")
                .build();

        employeeRepository.save(user1);
        employeeRepository.save(user2);

        //Act
        List<User> userList = employeeRepository.findAll();

        //Assert
        assertThat(userList).isNotNull();
        assertThat(userList.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    public void employeeRepository_getById_returnEmployeeByIdTest() {

        //Arrange
        User user = User.builder()
                .firstName("Chris")
                .lastName("Robin")
                .email("NkjnOJt@example.com")
                .build();
        employeeRepository.save(user);

        //Act
        User foundUser = employeeRepository.findById(user.getId()).get();

        //Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(user.getId());
    }

    @Test
    public void employeeRepository_getByEmail_returnEmployeeByEmailTest() {

        //Arrange
        User user = User.builder()
                .firstName("Chris")
                .lastName("Robin")
                .email("NkjnOJt@example.com")
                .build();
        employeeRepository.save(user);

        //Act
        User foundUser = employeeRepository.findByEmail(user.getEmail()).get();

        //Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void employeeRepository_deleteById_deleteEmployeeByIdTest() {
        //Arrange
        User user = User.builder()
                .firstName("Chris")
                .lastName("Robin")
                .email("NkjnOJt@example.com")
                .build();
        User savedUser = employeeRepository.save(user);

        //Act
        employeeRepository.deleteById(savedUser.getId());

        //Assert
        Optional<User> deletedEmployee = employeeRepository.findById(savedUser.getId());
        assertThat(deletedEmployee).isEmpty();
    }

    @Test
    public void employeeRepository_updateById_updateEmployeeByIdTest() {
        //Arrange
        User user = User.builder()
                .firstName("Mahil")
                .lastName("Robin")
                .email("NkjnOmahilJt@example.com")
                .build();
        User savedUser = employeeRepository.save(user);

        //Act
        savedUser.setId(savedUser.getId());
        savedUser.setFirstName("Mareen");
        savedUser.setLastName("Raj");
        savedUser.setEmail("N6OJt@example.com");
        User updatedUser = employeeRepository.save(savedUser);

        //Assert
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getFirstName()).isEqualTo("Mareen");
        assertThat(updatedUser.getLastName()).isEqualTo("Raj");
        assertThat(updatedUser.getEmail()).isEqualTo("N6OJt@example.com");
    }
}