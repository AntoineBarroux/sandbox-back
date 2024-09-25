package com.sandbox.interactive.employee.service;

import com.sandbox.interactive.employee.controller.requests.EmployeeCreateRequest;
import com.sandbox.interactive.employee.controller.requests.EmployeeUpdateRequest;
import com.sandbox.interactive.employee.exception.HierarchyException;
import com.sandbox.interactive.employee.exception.SupervisorNotFoundException;
import com.sandbox.interactive.employee.mapper.EmployeeMapper;
import com.sandbox.interactive.employee.repository.entity.EmployeeEntity;
import com.sandbox.interactive.employee.service.domain.EmployeeRepository;
import com.sandbox.interactive.employee.service.model.Employee;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(EmployeeRepository employeeRepository, final EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    public Page<Employee> getEmployees(PageRequest pageRequest) {
        return employeeRepository.findAll(pageRequest)
                .map(employeeMapper::toDomain);
    }

    public Employee createEmployee(EmployeeCreateRequest employeeCreateRequest) {
        final Optional<EmployeeEntity> supervisor = employeeCreateRequest.supervisorId().isPresent() ?
                employeeCreateRequest.supervisorId()
                        .map(employeeRepository::findById)
                        .filter(Optional::isPresent)
                        .orElseThrow(() -> new SupervisorNotFoundException("Supervisor not found")) :
                Optional.empty();

        return saveEmployee(employeeMapper.createToDomain(employeeCreateRequest, supervisor));
    }

    public Employee updateEmployee(EmployeeUpdateRequest employeeUpdateRequest) {
        if (!employeeRepository.existsById(employeeUpdateRequest.id())) {
            throw new EntityNotFoundException("Employee not found");
        }
        final Optional<EmployeeEntity> supervisor = employeeUpdateRequest.supervisorId().isPresent() ?
                employeeUpdateRequest.supervisorId()
                        .map(employeeRepository::findById)
                        .filter(Optional::isPresent)
                        .orElseThrow(() -> new SupervisorNotFoundException("Supervisor not found")) :
                Optional.empty();
        if (supervisor.isPresent() && supervisor.get().getId().equals(employeeUpdateRequest.id())) {
            throw new HierarchyException("Supervisor cannot be the employee himself !");
        }

        return saveEmployee(employeeMapper.updateToDomain(employeeUpdateRequest, supervisor));
    }

    private Employee saveEmployee(Employee employee) {
        return employeeMapper.toDomain(
                employeeRepository.save(employeeMapper.toEntity(employee))
        );
    }

    public void deleteEmployeeById(UUID employeeId) {
        employeeRepository.deleteById(employeeId);
    }
}
