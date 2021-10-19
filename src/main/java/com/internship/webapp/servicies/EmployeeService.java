package com.internship.webapp.servicies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.internship.webapp.model.Department;
import com.internship.webapp.model.Employee;
import com.internship.webapp.repositories.GenericRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {

    private final CrudRepository<Employee, Long> repository;

    private final ObjectWriter objectWriter;

    @Autowired
    public EmployeeService(CrudRepository<Employee, Long> repository) {
        this.repository = repository;
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    public List<Employee> findAll() {
        List<Employee> employeeList = new ArrayList<>();
        repository.findAll().forEach(employeeList::add);
        return employeeList;
    }

    public Employee save(Employee employee) {
        return repository.save(employee);
    }

    public ResponseEntity<String> findById(long id) {

        try {
            if (repository.existsById(id)) {
                Employee employee = repository.findById(id).get();
                return ResponseEntity.status(HttpStatus.OK).body(objectWriter.writeValueAsString(employee));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employee with id " + id + " not found!");
            }
        } catch (JsonProcessingException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in data processing!");
        }
    }

    public String deleteById(long id) {
        repository.deleteById(id);
        return "Employee with id " + id + " was successfully deleted!";
    }

    public ResponseEntity<String> updateById(long id, Employee employee) {
        try {

            employee.setId(id);

            Employee updatedEmployee = repository.findById(id).get();

            updatedEmployee.setFirstName(employee.getFirstName());
            updatedEmployee.setLastName(employee.getLastName());
            updatedEmployee.setEmail(employee.getEmail());
            updatedEmployee.setPhoneNumber(employee.getPhoneNumber());
            updatedEmployee.setDepartmentId(employee.getDepartmentId());
            updatedEmployee.setCommissionPct(employee.getCommissionPct());
            updatedEmployee.setSalary(employee.getSalary());
            updatedEmployee.setHireDate(employee.getHireDate());
            updatedEmployee.setManagerId(employee.getManagerId());
            updatedEmployee.setJobId(employee.getJobId());

            repository.save(updatedEmployee);

            return ResponseEntity.status(HttpStatus.OK).body(objectWriter.writeValueAsString(updatedEmployee));

        } catch (JsonProcessingException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in data processing!");
        } catch (NullPointerException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The department with this id not found!");
        }
    }
}
