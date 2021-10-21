package com.internship.webapp.servicies;

import com.internship.webapp.model.Employee;
import com.internship.webapp.repositories.GenericRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EmployeeService {

    private final GenericRepository<Employee> repository;

    public List<Employee> findAll() {
        return repository.findAll();
    }

    public Employee save(Employee employee) {
        return repository.save(employee);
    }

    public Employee findById(long id) {
        return repository.findById(id);
    }

    public Long deleteById(long id) {
        return repository.deleteById(id);
    }

    public Employee updateById(long id, Employee employee) {
        return repository.updateById(id, employee);
    }
}
