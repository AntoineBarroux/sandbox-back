package com.sandbox.interactive.employee.repository;

import com.sandbox.interactive.employee.repository.entity.EmployeeEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class JpaEmployeeRepositoryTest {

    @Autowired
    private JpaEmployeeRepository employeeRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
    }

    @Test
    void should_correctly_save_and_retrieve_employee_without_supervisor() {
        final var employee = new EmployeeEntity();
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setPosition("Software Developer");

        final var saved = employeeRepository.save(employee);
        entityManager.flush();
        final var retrieved = employeeRepository.findById(saved.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(saved);
        assertThat(retrieved.get().getCreatedAt()).isNotNull();
    }

    @Test
    void should_correctly_save_and_retrieve_employee_with_supervisor() {
        final var supervisor = new EmployeeEntity();
        supervisor.setFirstName("Jane");
        supervisor.setLastName("Doe");
        supervisor.setPosition("Engineers Manager");

        final var employee = new EmployeeEntity();
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setPosition("Software Developer");
        employee.setSupervisor(supervisor);

        employeeRepository.save(supervisor);
        final var saved = employeeRepository.save(employee);
        entityManager.flush();

        final var retrieved = employeeRepository.findById(saved.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(saved);
        assertThat(retrieved.get().getSupervisor()).isEqualTo(supervisor);
    }

    @Test
    void should_correctly_remove_supervisor() {
        final var supervisor = new EmployeeEntity();
        supervisor.setFirstName("Jane");
        supervisor.setLastName("Doe");
        supervisor.setPosition("Engineers Manager");

        final var employee = new EmployeeEntity();
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setPosition("Software Developer");
        employee.setSupervisor(supervisor);

        employeeRepository.save(supervisor);
        final var saved = employeeRepository.save(employee);
        entityManager.flush();

        final var retrieved = employeeRepository.findById(saved.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(saved);
        assertThat(retrieved.get().getSupervisor()).isEqualTo(supervisor);

        employee.setSupervisor(null);
        employeeRepository.save(employee);
        entityManager.flush();
        assertThat(employeeRepository.findById(saved.getId()).get().getSupervisor()).isNull();
    }
}