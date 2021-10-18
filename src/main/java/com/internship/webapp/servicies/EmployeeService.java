package com.internship.webapp.servicies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.internship.webapp.model.Employee;
import com.internship.webapp.model.Views;
import com.internship.webapp.repositories.GenericRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final GenericRepository<Employee> repository;

    private final ObjectWriter objectWriter;

    @Autowired
    public EmployeeService(GenericRepository<Employee> repository) {
        this.repository = repository;
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    public List<Employee> findAll() {
        return repository.findAll();
    }

    public ResponseEntity<String> save(Employee employee) {
        try {
            return repository.save(employee);
        }
        catch (JsonProcessingException exception) {
            System.out.println(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in data processing!");
        }
    }

    public ResponseEntity<String> findById(long id){
        Employee employee = repository.findById(id);
        if(employee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee with id " + id + " not found!");
        } else {
            try {
                return ResponseEntity.status(HttpStatus.OK).body(objectWriter.writeValueAsString(employee));
            } catch (JsonProcessingException exception) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in data processing!");
            }
        }
    }

    public String deleteById(long id) {
        return repository.deleteById(id);
    }

    public ResponseEntity<String> updateById(long id, Employee employee) {
        try {
            return repository.updateById(id, employee);
        }
        catch (JsonProcessingException exception) {
            System.out.println(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in data processing!");
        }
    }
}
