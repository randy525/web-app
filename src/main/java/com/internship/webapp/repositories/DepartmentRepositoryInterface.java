package com.internship.webapp.repositories;

import com.internship.webapp.model.Department;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepositoryInterface extends CrudRepository<Department, Long> {
}
