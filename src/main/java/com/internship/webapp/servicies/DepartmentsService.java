package com.internship.webapp.servicies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.internship.webapp.model.Department;
import com.internship.webapp.repositories.GenericRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentsService {

    private final GenericRepository<Department> repository;

    private final ObjectWriter objectWriter;

    @Autowired
    public DepartmentsService(GenericRepository<Department> repository) {
        this.repository = repository;
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    public List<Department> findAll() {
        return repository.findAll();
    }

    public ResponseEntity<String> save(Department department) {
        try {
            return repository.save(department);
        }
        catch (JsonProcessingException exception) {
            System.out.println(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in data processing!");
        }
    }

    public ResponseEntity<String> findById(long id){
        Department department = repository.findById(id);
        if(department == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee with id " + id + " not found!");
        } else {
            try {
                return ResponseEntity.status(HttpStatus.OK).body(objectWriter.writeValueAsString(department));
            } catch (JsonProcessingException exception) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in data processing!");
            }
        }
    }

    public String deleteById(long id) {
        return repository.deleteById(id);
    }

    public ResponseEntity<String> updateById(long id, Department department) {
        try {
            return repository.updateById(id, department);
        }
        catch (JsonProcessingException exception) {
            System.out.println(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in data processing!");
        }
    }
}
