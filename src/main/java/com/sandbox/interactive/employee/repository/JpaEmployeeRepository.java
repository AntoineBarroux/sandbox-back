package com.sandbox.interactive.employee.repository;

import com.sandbox.interactive.employee.repository.entity.EmployeeEntity;
import com.sandbox.interactive.employee.service.domain.EmployeeRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaEmployeeRepository extends ListCrudRepository<EmployeeEntity, UUID>,
        PagingAndSortingRepository<EmployeeEntity, UUID>,
        EmployeeRepository {
}
