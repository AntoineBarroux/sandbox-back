package com.sandbox.interactive.employee.repository;

import com.sandbox.interactive.employee.repository.entity.EmployeeEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface JpaEmployeeRepository extends ListCrudRepository<EmployeeEntity, UUID>, PagingAndSortingRepository<EmployeeEntity, UUID> {
}
