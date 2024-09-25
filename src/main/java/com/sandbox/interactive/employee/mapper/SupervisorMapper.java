package com.sandbox.interactive.employee.mapper;

import com.sandbox.interactive.employee.controller.dto.SupervisorDTO;
import com.sandbox.interactive.employee.service.model.Employee;
import org.mapstruct.Mapper;

import java.util.Optional;

@Mapper
public interface SupervisorMapper {

    SupervisorDTO toSupervisor(Employee employee);

    default Optional<SupervisorDTO> toSupervisor(Optional<Employee> employee) {
        return employee.map(this::toSupervisor);
    }
}
