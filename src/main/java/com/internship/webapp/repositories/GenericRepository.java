package com.internship.webapp.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T>{
    List<T> findAll();

    T save(T obj);

    T findById(long id);

    Long deleteById(long id);

    T updateById(long id, T obj);
}
