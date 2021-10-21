package com.internship.webapp.servicies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.internship.webapp.model.Department;
import com.internship.webapp.repositories.GenericRepository;
import com.internship.webapp.repositories.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DepartmentsService {

    private final GenericRepository<Department> repository;

    private final LocationRepository locationRepository;


    public List<Department> findAll() {
        return repository.findAll();
    }

    public Department save(Department department) {
        Long locationId = locationRepository.getIdByCity(department.getLocation());
        if (locationId == null) {
            return null;
        }
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
