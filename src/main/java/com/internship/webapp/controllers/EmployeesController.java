package com.internship.webapp.controllers;

import com.internship.webapp.model.Employee;
import com.internship.webapp.servicies.EmployeeService;
import lombok.RequiredArgsConstructor;
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
    public Employee addEmployee(@Valid @RequestBody Employee employee) {
        return service.save(employee);
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<String> getEmployee(@PathVariable long id) {
        return service.findById(id);
    }

    @DeleteMapping("/employees/{id}")
    public String deleteEmployee(@PathVariable long id) {
        return service.deleteById(id);
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<String> updateEmployee(@PathVariable long id, @Valid @RequestBody Employee employee) {
        return service.updateById(id, employee);
    }

}
