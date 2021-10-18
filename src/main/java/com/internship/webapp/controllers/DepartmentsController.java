package com.internship.webapp.controllers;

import com.internship.webapp.model.Department;
import com.internship.webapp.servicies.DepartmentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class DepartmentsController {

    private final DepartmentsService service;

    @GetMapping("/departments")
    public List<Department> getDepartment() {
        return service.findAll();
    }

    @PostMapping(value = "/departments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addDepartment(@Valid @RequestBody Department department) {
        return service.save(department);
    }

    @GetMapping("/departments/{id}")
    public ResponseEntity<String> getDepartmentById(@PathVariable long id) {
        return service.findById(id);
    }

    @DeleteMapping("/departments/{id}")
    public String deleteDepartment(@PathVariable long id) {
        return service.deleteById(id);
    }

    @PutMapping("/departments/{id}")
    public ResponseEntity<String> updateDepartment(@PathVariable long id, @Valid @RequestBody Department department) {
        return service.updateById(id, department);
    }
}
