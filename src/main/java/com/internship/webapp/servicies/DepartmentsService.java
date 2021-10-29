package com.internship.webapp.servicies;

import com.internship.webapp.model.Department;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DepartmentsService {

    private final CrudRepository<Department, Long> repository;

    public List<Department> findAll() {
        List<Department> departmentList = new ArrayList<>();
        repository.findAll().forEach(departmentList::add);
        return departmentList;
    }

    public Department save(Department department) {
        return repository.save(department);
    }

    public Department findById(long id) {
        return repository.findById(id).orElse(null);
    }

    public Long deleteById(long id) {
        repository.deleteById(id);
        return id;
    }

    public Department updateById(long id, Department department) {
        department.setId(id);

        if (repository.findById(id).isPresent()) {
            Department updatedDepartment = repository.findById(id).get();
            updatedDepartment.setLocation(department.getLocation());
            updatedDepartment.setDepartmentName(department.getDepartmentName());
            updatedDepartment.setManagerId(department.getManagerId());
            updatedDepartment.setLocationId(department.getLocationId());
            return repository.save(updatedDepartment);
        }
        return null;

    }
}
