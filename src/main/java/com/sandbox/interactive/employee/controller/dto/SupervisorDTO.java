package com.sandbox.interactive.employee.controller.dto;

import java.util.UUID;

public record SupervisorDTO(
        UUID id,
        String firstName,
        String lastName
) {
}
