package com.internship.webapp.servicies;

import com.internship.webapp.model.Department;
import com.internship.webapp.repositories.GenericRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DepartmentsService {

    private final GenericRepository<Department> repository;


    public List<Department> findAll() {
        return repository.findAll();
    }

    public Department save(Department department) {
        return repository.save(department);
    }

    public Department findById(long id){
        return repository.findById(id);
    }

    public Long deleteById(long id) {
        return repository.deleteById(id);
    }

    public Department updateById(long id, Department department) {
        return repository.updateById(id, department);
    }
}
