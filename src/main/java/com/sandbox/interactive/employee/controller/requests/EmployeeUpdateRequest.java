package com.sandbox.interactive.employee.controller.requests;

import java.util.UUID;

public record EmployeeUpdateRequest(
        UUID id,
        String firstName,
        String lastName,
        String position,
        UUID supervisorId
) {
}
