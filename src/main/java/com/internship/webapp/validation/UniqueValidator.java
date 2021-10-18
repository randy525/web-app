package com.internship.webapp.validation;

import com.internship.webapp.repositories.EmployeeRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UniqueValidator implements ConstraintValidator<UniqueField, String> {

    private EmployeeRepository repository;

    @Autowired
    private void setRepository(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Override
    public void initialize(UniqueField constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String fieldValue, ConstraintValidatorContext constraintValidatorContext) {
        List<String> valuesList = repository.findAll()
                .stream()
                .flatMap(employee -> Stream.of(employee.getEmail(), employee.getPhoneNumber()))
                .collect(Collectors.toList());
        return !valuesList.contains(fieldValue);
    }
}
