package com.sandbox.interactive.employee.controller.dto;

import com.sandbox.interactive.employee.service.model.Employee;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

public record EmployeeDTO(
        UUID id,
        ZonedDateTime createdAt,
        String firstName,
        String lastName,
        String position,
        Optional<Employee> supervisor
) { }
