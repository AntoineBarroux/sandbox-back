package com.sandbox.interactive.employee.mapper;

import com.sandbox.interactive.employee.repository.entity.EmployeeEntity;
import com.sandbox.interactive.employee.service.domain.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class EmployeeIdToEntityMapper {

    private final EmployeeRepository employeeRepository;

    public EmployeeIdToEntityMapper(final EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public EmployeeEntity fromId(UUID id) {
        if (Objects.nonNull(id)) {
            return employeeRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
        }
        return null;
    }

    public UUID toId(EmployeeEntity employeeEntity) {
        return employeeEntity.getId();
    }
}