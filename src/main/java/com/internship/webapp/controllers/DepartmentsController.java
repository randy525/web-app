package com.internship.webapp.controllers;

import com.internship.webapp.model.Department;
import com.internship.webapp.servicies.DepartmentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public List<Department> getAllDepartments() {
        return service.findAll();
    }

    @PostMapping(value = "/departments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Department> addDepartment(@Valid @RequestBody Department department) {
        Department responseDepartment = service.save(department);
        if (responseDepartment != null) {
            return ResponseEntity.status(HttpStatus.OK).body(responseDepartment);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/departments/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable long id) {
        Department responseDepartment = service.findById(id);

        if (responseDepartment != null) {
            return ResponseEntity.status(HttpStatus.OK).body(responseDepartment);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }

    @DeleteMapping("/departments/{id}")
    public Long deleteDepartment(@PathVariable long id) {
        return service.deleteById(id);
    }

    @PutMapping("/departments/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable long id, @Valid @RequestBody Department department) {
        Department responseDepartment = service.updateById(id, department);

        if (responseDepartment != null) {
            return ResponseEntity.status(HttpStatus.OK).body(responseDepartment);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
