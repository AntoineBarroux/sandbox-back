package com.sandbox.interactive.employee.mapper;

import com.sandbox.interactive.employee.controller.dto.EmployeeDTO;
import com.sandbox.interactive.employee.controller.requests.EmployeeCreateRequest;
import com.sandbox.interactive.employee.controller.requests.EmployeeUpdateRequest;
import com.sandbox.interactive.employee.repository.entity.EmployeeEntity;
import com.sandbox.interactive.employee.service.model.Employee;
import org.mapstruct.Mapper;

import java.util.Optional;

@Mapper
public interface EmployeeMapper {

    EmployeeEntity toEntity(Employee employee);
    Employee toDomain(EmployeeEntity employee);
    EmployeeDTO toDTO(Employee employee);

    default Employee createToDomain(EmployeeCreateRequest employeeCreateRequest, Optional<EmployeeEntity> supervisor) {
        return supervisor.map(this::toDomain)
                .map(supervisorEntity -> Employee.createWithSupervisor(
                        employeeCreateRequest.firstName(),
                        employeeCreateRequest.lastName(),
                        employeeCreateRequest.position(),
                        supervisorEntity
                )).orElseGet(() -> Employee.createWithoutSupervisor(
                        employeeCreateRequest.firstName(),
                        employeeCreateRequest.lastName(),
                        employeeCreateRequest.position()
                ));
    }

    default Employee updateToDomain(EmployeeUpdateRequest employeeUpdateRequest, Optional<EmployeeEntity> supervisor) {
        return supervisor.map(this::toDomain)
                .map(supervisorEntity -> Employee.updateWithSupervisor(
                        employeeUpdateRequest.id(),
                        employeeUpdateRequest.firstName(),
                        employeeUpdateRequest.lastName(),
                        employeeUpdateRequest.position(),
                        supervisorEntity
                )).orElseGet(() -> Employee.updateWithoutSupervisor(
                        employeeUpdateRequest.id(),
                        employeeUpdateRequest.firstName(),
                        employeeUpdateRequest.lastName(),
                        employeeUpdateRequest.position()
                ));
    }

    default EmployeeEntity toSupervisor(Optional<Employee> supervisor) {
        return supervisor.map(this::toEntity).orElse(null);
    }

    default Optional<Employee> toSupervisor(EmployeeEntity supervisor) {
        return Optional.ofNullable(supervisor).map(this::toDomain);
    }
}
