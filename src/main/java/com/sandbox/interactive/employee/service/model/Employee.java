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

    public static Employee createWithSupervisor(String firstName, String lastName, String position, Employee supervisor) {
        return new Employee(null, null, firstName, lastName, position, Optional.of(supervisor));
    }

    public static Employee createWithoutSupervisor(String firstName, String lastName, String position) {
        return new Employee(null, null, firstName, lastName, position, Optional.empty());
    }

    public static Employee updateWithSupervisor(UUID id, String firstName, String lastName, String position, Employee supervisor) {
        return new Employee(id, null, firstName, lastName, position, Optional.of(supervisor));
    }

    public static Employee updateWithoutSupervisor(UUID id, String firstName, String lastName, String position) {
        return new Employee(id, null, firstName, lastName, position, Optional.empty());
    }
}
