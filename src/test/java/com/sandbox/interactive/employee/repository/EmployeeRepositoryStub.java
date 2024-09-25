package com.sandbox.interactive.employee.repository;

import com.sandbox.interactive.employee.repository.entity.EmployeeEntity;
import com.sandbox.interactive.employee.service.domain.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

public class EmployeeRepositoryStub implements EmployeeRepository {

    private final List<EmployeeEntity> employees = new ArrayList<>();

    @Override
    public Page<EmployeeEntity> findAll(final Pageable pageable) {
        return new PageImpl<>(employees.subList(pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize()));
    }

    @Override
    public Optional<EmployeeEntity> findById(final UUID id) {
        return employees.stream()
                .filter(employee -> employee.getId().equals(id))
                .findFirst();
    }

    @Override
    public EmployeeEntity save(final EmployeeEntity employee) {
        if (existsById(employee.getId())) {
            deleteById(employee.getId());
        }
        if (Objects.isNull(employee.getId())) {
            employee.setId(UUID.randomUUID());
        }
        employees.add(employee);
        return employee;
    }

    @Override
    public void deleteById(final UUID id) {
        employees.removeIf(employee -> employee.getId().equals(id));
    }

    @Override
    public boolean existsById(final UUID id) {
        return employees.stream()
                .anyMatch(employee -> employee.getId().equals(id));
    }

    @Override
    public void deleteAll() {
        employees.clear();
    }
}
