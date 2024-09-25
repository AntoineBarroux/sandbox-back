package com.sandbox.interactive.employee.service.domain;

import com.sandbox.interactive.employee.repository.entity.EmployeeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface Employees {
    Page<EmployeeEntity> findAll(Pageable pageable);
    Optional<EmployeeEntity> findById(UUID id);
    EmployeeEntity save(EmployeeEntity employee);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
