package com.sandbox.interactive.employee.service;

import com.sandbox.interactive.employee.controller.requests.EmployeeCreateRequest;
import com.sandbox.interactive.employee.controller.requests.EmployeeUpdateRequest;
import com.sandbox.interactive.employee.exception.HierarchyException;
import com.sandbox.interactive.employee.exception.SupervisorNotFoundException;
import com.sandbox.interactive.employee.mapper.EmployeeMapperImpl;
import com.sandbox.interactive.employee.repository.EmployeeRepositoryStub;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class EmployeeServiceTest {

    private EmployeeService employeeService;

    @BeforeEach
    void setup() {
        employeeService = new EmployeeService(new EmployeeRepositoryStub(), new EmployeeMapperImpl());
    }

    @Test
    void should_correctly_create() {
        final var employeeCreateRequest = new EmployeeCreateRequest("John", "Doe", "Software Developer", Optional.empty());
        final var saved = employeeService.createEmployee(employeeCreateRequest);
        assertThat(saved).isNotNull();
        assertThat(saved.firstName()).isEqualTo("John");
        assertThat(saved.lastName()).isEqualTo("Doe");
        assertThat(saved.position()).isEqualTo("Software Developer");
        assertThat(saved.supervisor()).isEmpty();
    }

    @Test
    void should_correctly_throw_if_creating_employee_with_inexistant_supervisor() {
        final var employeeCreateRequest = new EmployeeCreateRequest("John", "Doe", "Software Developer", Optional.of(UUID.randomUUID()));
        Assertions.assertThatThrownBy(() -> employeeService.createEmployee(employeeCreateRequest))
                .isInstanceOf(SupervisorNotFoundException.class)
                .hasMessage("Supervisor not found");
    }

    @Test
    void should_correctly_update() {
        final var employeeCreateRequest = new EmployeeCreateRequest("John", "Doe", "Software Developer", Optional.empty());
        final var saved = employeeService.createEmployee(employeeCreateRequest);
        final var employeeUpdateRequest = new EmployeeUpdateRequest(saved.id(), "Jane", "Doe", "Software Developer", Optional.empty());
        final var updated = employeeService.updateEmployee(employeeUpdateRequest);

        assertThat(saved.id()).isEqualTo(updated.id());
        assertThat(updated.firstName()).isEqualTo("Jane");
    }

    @Test
    void should_correctly_throw_if_updating_non_existing_employee() {
        final var employeeUpdateRequest = new EmployeeUpdateRequest(UUID.randomUUID(), "John", "Doe", "Software Developer", Optional.empty());
        Assertions.assertThatThrownBy(() -> employeeService.updateEmployee(employeeUpdateRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Employee not found");
    }

    @Test
    void should_correctly_throw_if_updating_employee_with_inexistant_supervisor() {
        final var employeeCreateRequest = new EmployeeCreateRequest("John", "Doe", "Software Developer", Optional.empty());
        final var saved = employeeService.createEmployee(employeeCreateRequest);

        final var employeeUpdateRequest = new EmployeeUpdateRequest(saved.id(), "Jane", "Doe", "Software Developer", Optional.of(UUID.randomUUID()));
        Assertions.assertThatThrownBy(() -> employeeService.updateEmployee(employeeUpdateRequest))
                .isInstanceOf(SupervisorNotFoundException.class)
                .hasMessage("Supervisor not found");
    }

    @Test
    void should_correctly_throw_if_updating_employee_with_himself_as_supervisor() {
        final var employeeCreateRequest = new EmployeeCreateRequest("John", "Doe", "Software Developer", Optional.empty());
        final var saved = employeeService.createEmployee(employeeCreateRequest);

        final var employeeUpdateRequest = new EmployeeUpdateRequest(saved.id(), "Jane", "Doe", "Software Developer", Optional.of(saved.id()));
        Assertions.assertThatThrownBy(() -> employeeService.updateEmployee(employeeUpdateRequest))
                .isInstanceOf(HierarchyException.class)
                .hasMessage("Supervisor cannot be the employee himself !");
    }
}