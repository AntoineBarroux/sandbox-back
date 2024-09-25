package com.sandbox.interactive.employee.service;

import com.sandbox.interactive.employee.mapper.EmployeeMapper;
import com.sandbox.interactive.employee.service.domain.Employees;
import com.sandbox.interactive.employee.service.model.Employee;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmployeeService {

    private final Employees employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(Employees employeeRepository, final EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    public Page<Employee> getEmployees(PageRequest pageRequest) {
        return employeeRepository.findAll(pageRequest)
                .map(employeeMapper::toDomain);
    }

    public Employee saveEmployee(Employee employee) {
        return employeeMapper.toDomain(
                employeeRepository.save(employeeMapper.toEntity(employee))
        );
    }

    public Employee updateEmployee(Employee employee) {
        if (!employeeRepository.existsById(employee.id())) {
            throw new EntityNotFoundException("Employee not found");
        }
        return saveEmployee(employee);
    }

    public void deleteEmployeeById(UUID employeeId) {
        employeeRepository.deleteById(employeeId);
    }
}
