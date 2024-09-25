package com.sandbox.interactive.employee.service;

import com.sandbox.interactive.employee.controller.requests.EmployeeCreateRequest;
import com.sandbox.interactive.employee.controller.requests.EmployeeUpdateRequest;
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
    void should_correctly_throw_if_updating_non_existing_employee() {
        // given
        final var employeeUpdateRequest = new EmployeeUpdateRequest(UUID.randomUUID(), "John", "Doe", "Software Developer", Optional.empty());
        Assertions.assertThatThrownBy(() -> employeeService.updateEmployee(employeeUpdateRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Employee not found");
    }

    @Test
    void should_correctly_update_if_employee_exists() {
        // given
        final var employeeCreateRequest = new EmployeeCreateRequest("John", "Doe", "Software Developer", Optional.empty());
        final var saved = employeeService.createEmployee(employeeCreateRequest);

        final var employeeUpdateRequest = new EmployeeUpdateRequest(saved.id(), "Jane", "Doe", "Software Developer", Optional.empty());
        // when
        final var updated = employeeService.updateEmployee(employeeUpdateRequest);

        // then
        assertThat(saved.id()).isEqualTo(updated.id());
        assertThat(updated.firstName()).isEqualTo("Jane");
    }
}