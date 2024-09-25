package com.sandbox.interactive.employee.controller.dto;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

public record EmployeeDTO(
        UUID id,
        ZonedDateTime createdAt,
        String firstName,
        String lastName,
        String position,
        Optional<SupervisorDTO> supervisor
) { }
