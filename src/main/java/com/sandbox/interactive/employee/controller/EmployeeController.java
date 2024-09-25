package com.sandbox.interactive.employee.controller;

import com.sandbox.interactive.employee.controller.dto.EmployeeDTO;
import com.sandbox.interactive.employee.controller.requests.EmployeeCreateRequest;
import com.sandbox.interactive.employee.controller.requests.EmployeeUpdateRequest;
import com.sandbox.interactive.employee.exception.UpdateMismatchIdsException;
import com.sandbox.interactive.employee.mapper.EmployeeMapper;
import com.sandbox.interactive.employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;

    public EmployeeController(final EmployeeService employeeService, final EmployeeMapper employeeMapper) {
        this.employeeService = employeeService;
        this.employeeMapper = employeeMapper;
    }

    @GetMapping
    @Operation(summary = "Get a paginated list of employees")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched employees", content = { @Content(mediaType = "application/json") })
    })
    public ResponseEntity<Page<EmployeeDTO>> findAll(@ParameterObject @RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "10") int size) {
        LOG.info("Calling GET employees with page {} and size {}", page, size);

        return ResponseEntity.ok()
                .body(employeeService.getEmployees(PageRequest.of(page, size))
                        .map(employeeMapper::toDTO));
    }

    @PostMapping
    @Operation(summary = "Creates an employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee successfully created", content = { @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "The employee's supervisor does not exist", content = { @Content(mediaType = "application/json") }),
    })
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeCreateRequest employeeCreateRequest) {
        LOG.info("Calling POST employee");
        LOG.debug("Payload : {}", employeeCreateRequest);

        final var created = employeeMapper.toDTO(
                employeeService.createEmployee(employeeCreateRequest)
        );
        return ResponseEntity.created(URI.create("/employees/" + created.id()))
                .body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Updates an existing employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee successfully updated", content = { @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "404",
                    description = "The employee you're trying to update does not exists, or the employee's supervisor does not exist",
                    content = { @Content(mediaType = "application/json") }
            ),
            @ApiResponse(responseCode = "400", description = "Ids in path and payload does not match", content = { @Content(mediaType = "application/json") }),
    })
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable("id") UUID id, @RequestBody EmployeeUpdateRequest employeeUpdateRequest) {
        LOG.info("Calling PUT employee with id {}", id);
        LOG.debug("Payload : {}", employeeUpdateRequest);

        if (!id.equals(employeeUpdateRequest.id())) {
            throw new UpdateMismatchIdsException("The id in the path and the payload do not match");
        }

        return ResponseEntity.ok()
                .body(employeeMapper.toDTO(
                        employeeService.updateEmployee(employeeUpdateRequest)
                ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes an employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted employee. If the employee does not exist, you still get this status code.", content = { @Content(mediaType = "application/json") })
    })
    public ResponseEntity<Void> deleteEmployee(@PathVariable("id") UUID id) {
        LOG.info("Calling DELETE employee with id {}", id);

        employeeService.deleteEmployeeById(id);
        return ResponseEntity.noContent().build();
    }
}
