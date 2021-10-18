package com.internship.webapp.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T>{
    List<T> findAll();

    ResponseEntity<String> save(T obj) throws JsonProcessingException;

    T findById(long id);

    String deleteById(long id);

    ResponseEntity<String> updateById(long id, T obj) throws JsonProcessingException;
}
