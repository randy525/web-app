package com.internship.webapp.servicies;

import com.internship.webapp.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EmployeeService {

    private final CrudRepository<Employee, Long> repository;

    public List<Employee> findAll() {
        List<Employee> employeeList = new ArrayList<>();
        repository.findAll().forEach(employeeList::add);
        return employeeList;
    }

    public Employee save(Employee employee) {
        return repository.save(employee);
    }

    public Employee findById(long id) {

        if (repository.existsById(id)) {
            return repository.findById(id).get();
        }

        return null;
    }

    public Long deleteById(long id) {
        repository.deleteById(id);
        return id;
    }

    public Employee updateById(long id, Employee employee) {

        employee.setId(id);

        if (repository.findById(id).isPresent()) {
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

            return repository.save(updatedEmployee);
        }
        return null;
    }
}
