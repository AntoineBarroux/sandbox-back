package com.sandbox.interactive.employee.mapper;

import com.sandbox.interactive.employee.controller.dto.EmployeeDTO;
import com.sandbox.interactive.employee.controller.requests.EmployeeCreateRequest;
import com.sandbox.interactive.employee.controller.requests.EmployeeUpdateRequest;
import com.sandbox.interactive.employee.repository.entity.EmployeeEntity;
import com.sandbox.interactive.employee.service.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(uses = { EmployeeIdToEntityMapper.class})
public interface EmployeeMapper {

    EmployeeEntity toEntity(Employee employee);
    Employee toDomain(EmployeeEntity employee);
    EmployeeDTO toDTO(Employee employee);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "supervisor", source = "supervisorId")
    Employee createToDomain(EmployeeCreateRequest employeeCreateRequest);

    default Employee updateToDomain(EmployeeUpdateRequest employeeUpdateRequest, Optional<EmployeeEntity> supervisor) {
        return new Employee(
                employeeUpdateRequest.id(),
                null,
                employeeUpdateRequest.firstName(),
                employeeUpdateRequest.lastName(),
                employeeUpdateRequest.position(),
                supervisor.map(this::toDomain)
        );
    }

    default EmployeeEntity toSupervisor(Optional<Employee> supervisor) {
        return supervisor.map(this::toEntity).orElse(null);
    }

    default Optional<Employee> toSupervisor(EmployeeEntity supervisor) {
        return Optional.ofNullable(supervisor).map(this::toDomain);
    }
}
