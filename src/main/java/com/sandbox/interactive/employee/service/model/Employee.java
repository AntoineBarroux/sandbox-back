package com.sandbox.interactive.employee.service.model;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

public record Employee(
        UUID id,
        ZonedDateTime createdAt,
        String firstName,
        String lastName,
        String position,
        Optional<Employee> supervisor
) {

}
