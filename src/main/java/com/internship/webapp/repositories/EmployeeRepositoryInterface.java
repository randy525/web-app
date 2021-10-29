package com.internship.webapp.repositories;

import com.internship.webapp.model.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepositoryInterface extends CrudRepository<Employee, Long> {
}
