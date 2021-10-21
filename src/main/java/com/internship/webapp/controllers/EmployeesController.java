package com.internship.webapp.controllers;

import com.internship.webapp.model.Department;
import com.internship.webapp.model.Employee;
import com.internship.webapp.servicies.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class EmployeesController {

    private final EmployeeService service;

    @GetMapping("/employees")
    public List<Employee> getEmployees() {
        return service.findAll();
    }

    @PostMapping(value = "/employees", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Employee> addEmployee(@Valid @RequestBody Employee employee) {
        Employee responseEmployee = service.save(employee);
        if (responseEmployee != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(responseEmployee);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> getEmployee(@PathVariable long id) {
        Employee responseEmployee = service.findById(id);
        if (responseEmployee != null) {
            return ResponseEntity.status(HttpStatus.OK).body(responseEmployee);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @DeleteMapping("/employees/{id}")
    public Long deleteEmployee(@PathVariable long id) {
        return service.deleteById(id);
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable long id, @Valid @RequestBody Employee employee) {
        Employee responseEmployee = service.updateById(id, employee);
        if (responseEmployee != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(responseEmployee);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

}
