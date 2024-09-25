package com.sandbox.interactive.employee.repository;

import com.sandbox.interactive.employee.repository.entity.EmployeeEntity;
import com.sandbox.interactive.employee.service.domain.Employees;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class EmployeeRepository implements Employees {

    private final JpaEmployeeRepository jpaEmployeeRepository;

    public EmployeeRepository(final JpaEmployeeRepository jpaEmployeeRepository) {
        this.jpaEmployeeRepository = jpaEmployeeRepository;
    }

    @Override
    public Page<EmployeeEntity> findAll(final Pageable pageable) {
        return jpaEmployeeRepository.findAll(pageable);
    }

    @Override
    public Optional<EmployeeEntity> findById(final UUID id) {
        return jpaEmployeeRepository.findById(id);
    }

    @Override
    public EmployeeEntity save(final EmployeeEntity employee) {
        return jpaEmployeeRepository.save(employee);
    }

    @Override
    public void deleteById(final UUID id) {
        jpaEmployeeRepository.deleteById(id);
    }

    @Override
    public boolean existsById(final UUID id) {
        return jpaEmployeeRepository.existsById(id);
    }
}
