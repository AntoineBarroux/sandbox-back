package com.sandbox.interactive.employee.controller;

import com.sandbox.interactive.employee.mapper.EmployeeIdToEntityMapper;
import com.sandbox.interactive.employee.mapper.EmployeeMapperImpl;
import com.sandbox.interactive.employee.repository.entity.EmployeeEntity;
import com.sandbox.interactive.employee.service.EmployeeService;
import com.sandbox.interactive.employee.service.domain.EmployeeRepository;
import com.sandbox.interactive.employee.service.model.Employee;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private EmployeeIdToEntityMapper employeeIdToEntityMapper;

    @SpyBean
    private EmployeeMapperImpl employeeMapper;

    @MockBean
    private EmployeeRepository employeeRepository;

    @MockBean
    private EmployeeService employeeService;

    @Test
    void should_correctly_return_paginated_employees() throws Exception {
        final var manager = new Employee(UUID.randomUUID(), ZonedDateTime.now(), "John", "Doe", "Manager", Optional.empty());
        final var employee = new Employee(UUID.randomUUID(), ZonedDateTime.now(), "Jane", "Doe", "Developer", Optional.of(manager));

        when(employeeService.getEmployees(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(manager, employee), PageRequest.of(0, 10), 2));

        mockMvc.perform(get("/employee?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray()).andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").isNotEmpty()).andExpect(jsonPath("$.content[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$.content[0].firstName").value("John")).andExpect(jsonPath("$.content[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.content[0].position").value("Manager")).andExpect(jsonPath("$.content[0].supervisor").isEmpty())
                .andExpect(jsonPath("$.content[1].id").isNotEmpty()).andExpect(jsonPath("$.content[1].createdAt").isNotEmpty())
                .andExpect(jsonPath("$.content[1].firstName").value("Jane")).andExpect(jsonPath("$.content[1].lastName").value("Doe"))
                .andExpect(jsonPath("$.content[1].position").value("Developer")).andExpect(jsonPath("$.content[1].supervisor").isNotEmpty());

        final var captor = ArgumentCaptor.forClass(PageRequest.class);
        verify(employeeService).getEmployees(captor.capture());
        assertThat(captor.getValue().getPageNumber()).isEqualTo(0);
        assertThat(captor.getValue().getPageSize()).isEqualTo(10);
    }

    @Test
    void should_correctly_create_employee() throws Exception {
        final var employee = new Employee(UUID.randomUUID(), ZonedDateTime.now(), "John", "Doe", "Manager", Optional.empty());
        when(employeeService.saveEmployee(any(Employee.class))).thenReturn(employee);

        mockMvc.perform(post("/employee").content("""
                         {
                              "firstName": "John",
                              "lastName": "Doe",
                              "position": "Manager",
                              "supervisorId": null
                        }""").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.createdAt").isNotEmpty()).andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe")).andExpect(jsonPath("$.position").value("Manager"))
                .andExpect(jsonPath("$.supervisor").isEmpty());
    }

    @Test
    void should_correctly_create_employee_with_supervisor() throws Exception {
        final var managerId = UUID.randomUUID();
        final var managerEntity = new EmployeeEntity();
        managerEntity.setCreatedAt(ZonedDateTime.now());
        managerEntity.setFirstName("John");
        managerEntity.setLastName("Doe");
        managerEntity.setPosition("Manager");
        managerEntity.setId(managerId);
        final var manager = employeeMapper.toDomain(managerEntity);
        final var employee = new Employee(UUID.randomUUID(), ZonedDateTime.now(), "Jane", "Doe", "Developer", Optional.of(manager));
        when(employeeRepository.findById(any(UUID.class))).thenReturn(Optional.of(managerEntity));
        when(employeeService.saveEmployee(any(Employee.class))).thenReturn(employee);

        mockMvc.perform(post("/employee").content("""
                         {
                              "firstName": "Jane",
                              "lastName": "Doe",
                              "position": "Developer",
                              "supervisorId": "%s"
                        }""".formatted(managerId.toString())).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.createdAt").isNotEmpty()).andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe")).andExpect(jsonPath("$.position").value("Developer"))
                .andExpect(jsonPath("$.supervisor.id").value(managerId.toString()));
    }

    @Test
    void should_get_not_found_if_supervisor_cant_be_found() throws Exception {
        final var managerId = UUID.randomUUID();

        mockMvc.perform(post("/employee").content("""
                 {
                      "firstName": "Jane",
                      "lastName": "Doe",
                      "position": "Developer",
                      "supervisorId": "%s"
                }""".formatted(managerId.toString())).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    void should_correctly_update_employee() throws Exception {
        final var employee = new Employee(UUID.randomUUID(), ZonedDateTime.now(), "John", "Doe", "Manager", Optional.empty());
        when(employeeService.updateEmployee(any(Employee.class))).thenReturn(employee);

        mockMvc.perform(put("/employee/" + employee.id()).content("""
                         {
                              "id": "%s",
                              "firstName": "Jane",
                              "lastName": "Doe",
                              "position": "Developer",
                              "supervisorId": null
                        }""".formatted(employee.id().toString())).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.createdAt").isNotEmpty()).andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe")).andExpect(jsonPath("$.position").value("Manager"))
                .andExpect(jsonPath("$.supervisor").isEmpty());
    }

    @Test
    void should_get_bad_request_if_ids_mismatch() throws Exception {
        final var employee = new Employee(UUID.randomUUID(), ZonedDateTime.now(), "John", "Doe", "Manager", Optional.empty());

        mockMvc.perform(put("/employee/" + UUID.randomUUID()).content("""
                 {
                      "id": "%s",
                      "firstName": "Jane",
                      "lastName": "Doe",
                      "position": "Developer",
                      "supervisorId": null
                }""".formatted(employee.id().toString())).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    void should_get_not_found_if_employee_does_not_exist() throws Exception {
        final var employee = new Employee(UUID.randomUUID(), ZonedDateTime.now(), "John", "Doe", "Manager", Optional.empty());
        when(employeeService.updateEmployee(any(Employee.class))).thenThrow(new EntityNotFoundException("Employee not found"));

        mockMvc.perform(put("/employee/" + employee.id()).content("""
                 {
                      "id": "%s",
                      "firstName": "Jane",
                      "lastName": "Doe",
                      "position": "Developer",
                      "supervisorId": null
                }""".formatted(employee.id().toString())).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    void should_correctly_delete_employee() throws Exception {
        mockMvc.perform(delete("/employee/" + UUID.randomUUID())).andExpect(status().isNoContent());
    }
}