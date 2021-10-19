package com.internship.webapp.servicies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.internship.webapp.model.Department;
import com.internship.webapp.repositories.GenericRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DepartmentsService {

    private final CrudRepository<Department, Long> repository;

    private final ObjectWriter objectWriter;

    @Autowired
    public DepartmentsService(CrudRepository<Department, Long> repository) {
        this.repository = repository;
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    public List<Department> findAll() {
        List<Department> departmentList = new ArrayList<>();
        repository.findAll().forEach(departmentList::add);
        return departmentList;
    }

    public Department save(Department department) {
        return repository.save(department);
    }

    public ResponseEntity<String> findById(long id) {
        try {
            if (repository.existsById(id)) {
                Department department = repository.findById(id).get();
                return ResponseEntity.status(HttpStatus.OK).body(objectWriter.writeValueAsString(department));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employee with id " + id + " not found!");
            }
        } catch (JsonProcessingException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in data processing!");
        }

    }

    public String deleteById(long id) {
        repository.deleteById(id);
        return "Department with id " + id + " was deleted successfully!";
    }

    public ResponseEntity<String> updateById(long id, Department department) {
        try {
            department.setId(id);
            Department updatedDepartment = repository.findById(id).get();
            updatedDepartment.setLocation(department.getLocation());
            updatedDepartment.setDepartmentName(department.getDepartmentName());
            updatedDepartment.setManagerId(department.getManagerId());
            updatedDepartment.setLocationId(department.getLocationId());
            repository.save(updatedDepartment);

            return ResponseEntity.status(HttpStatus.OK).body(objectWriter.writeValueAsString(updatedDepartment));

        } catch (JsonProcessingException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in data processing!");
        } catch (NullPointerException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The department with this id not found!");
        }
    }
}
