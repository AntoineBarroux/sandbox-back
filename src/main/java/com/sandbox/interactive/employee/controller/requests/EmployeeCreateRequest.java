package com.sandbox.interactive.employee.controller.requests;

import java.util.Optional;
import java.util.UUID;

public record EmployeeCreateRequest(
        String firstName,
        String lastName,
        String position,
        Optional<UUID> supervisorId
) { }
