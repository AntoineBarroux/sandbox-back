package com.sandbox.interactive.employee.e2e;

import com.sandbox.interactive.employee.controller.dto.EmployeeDTO;
import com.sandbox.interactive.employee.repository.JpaEmployeeRepository;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EmployeeE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private JpaEmployeeRepository employeeRepository;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost:" + port;
        employeeRepository.deleteAll();
    }

    @Test
    void should_correctly_create_and_retrieve_users() {
        final var employee = createEmployee();
        final var employees = findAllEmployees(0, 10);
        assertThat(employees).containsExactly(employee);
    }

    @Test
    void should_correctly_paginate_results() {
        final var employee = createEmployee();
        final var manager = createManager();
        assertThat(findAllEmployees(0, 10)).containsExactlyInAnyOrder(employee, manager);
        assertThat(findAllEmployees(0, 1)).containsExactly(employee);
        assertThat(findAllEmployees(1, 1)).containsExactly(manager);
    }

    @Test
    void should_correctly_update_employee() {
        final var employee = createEmployee();
        final var manager = createManager();
        final var updated = updateEmployee(employee.id(), manager.id());
        assertThat(updated).isNotEqualTo(employee);
    }

    @Test
    void should_correctly_handles_update_mismatch_ids_error() {
        final var employee = createEmployee();
        final var manager = createManager();

        final var updatedEmployee = new FixtureReader().read("employee/update-employee.json")
                .replace("%id%", employee.id().toString())
                .replace("%supervisorId%", manager.id().toString());

        RestAssured.given()
                .contentType("application/json")
                .body(updatedEmployee)
                .when()
                .put("/employee/" + UUID.randomUUID())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    void should_correctly_handles_update_employee_does_not_exist_error() {
        final var manager = createManager();
        final var randomId = UUID.randomUUID();
        final var updatedEmployee = new FixtureReader().read("employee/update-employee.json")
                .replace("%id%", randomId.toString())
                .replace("%supervisorId%", manager.id().toString());

        RestAssured.given()
                .contentType("application/json")
                .body(updatedEmployee)
                .when()
                .put("/employee/" + randomId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void should_correctly_handles_update_employee_supervisor_does_not_exist_error() {
        final var employee = createEmployee();

        final var updatedEmployee = new FixtureReader().read("employee/update-employee.json")
                .replace("%id%", employee.id().toString())
                .replace("%supervisorId%", UUID.randomUUID().toString());

        RestAssured.given()
                .contentType("application/json")
                .body(updatedEmployee)
                .when()
                .put("/employee/" + employee.id())
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void should_correctly_handles_create_employee_supervisor_does_not_exist_error() {
        final var employee = new FixtureReader().read("employee/create-employee-with-supervisor.json")
                .replace("%supervisorId%", UUID.randomUUID().toString());
        RestAssured.given()
                .contentType("application/json")
                .body(employee)
                .when()
                .post("/employee")
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void should_correctly_delete_employee() {
        final var employee = createEmployee();
        assertThat(findAllEmployees(0, 10)).isNotEmpty();
        deleteEmployee(employee.id());
        assertThat(findAllEmployees(0, 10)).isEmpty();
    }

    private EmployeeDTO createEmployee() {
        final var employee = new FixtureReader().read("employee/create-employee.json");
        return RestAssured.given()
                .contentType("application/json")
                .body(employee)
                .when()
                .post("/employee")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .and()
                .body("id", Matchers.notNullValue())
                .body("firstName", Matchers.is("Antoine"))
                .body("lastName", Matchers.is("Barroux"))
                .body("position", Matchers.is("Fullstack developer"))
                .body("supervisor", Matchers.nullValue())
                .and()
                .extract().body().jsonPath().getObject("", EmployeeDTO.class);
    }

    private EmployeeDTO createManager() {
        final var employee = new FixtureReader().read("employee/create-manager.json");
        return RestAssured.given()
                .contentType("application/json")
                .body(employee)
                .when()
                .post("/employee")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .and()
                .body("id", Matchers.notNullValue())
                .body("firstName", Matchers.is("Jane"))
                .body("lastName", Matchers.is("Doe"))
                .body("position", Matchers.is("Engineering manager"))
                .body("supervisor", Matchers.nullValue())
                .and()
                .extract().body().jsonPath().getObject("", EmployeeDTO.class);
    }

    private List<EmployeeDTO> findAllEmployees(int page, int size) {
        return RestAssured.given()
                .contentType("application/json")
                .when()
                .get("/employee?page=" + page + "&size=" + size)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .extract()
                .body()
                .jsonPath()
                .getList("content", EmployeeDTO.class);
    }

    private EmployeeDTO updateEmployee(UUID employeeId, UUID managerId) {
        final var updatedEmployee = new FixtureReader().read("employee/update-employee.json")
                .replace("%id%", employeeId.toString())
                .replace("%supervisorId%", managerId.toString());

        return RestAssured.given()
                .contentType("application/json")
                .body(updatedEmployee)
                .when()
                .put("/employee/" + employeeId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("id", Matchers.is(employeeId.toString()))
                .body("firstName", Matchers.is("John"))
                .body("lastName", Matchers.is("Doe"))
                .body("position", Matchers.is("Devops engineer"))
                .body("supervisor", Matchers.notNullValue())
                .and()
                .extract().body().jsonPath().getObject("", EmployeeDTO.class);
    }

    private void deleteEmployee(UUID employeeId) {
        RestAssured.given()
                .contentType("application/json")
                .when()
                .delete("/employee/" + employeeId)
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }
}
