package com.internship.webapp.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.internship.webapp.model.Department;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DepartmentsRepository implements GenericRepository<Department> {

    private final DataSource dataSource;


    public DepartmentsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Department> findAll() {
        try (Connection connection = DataSourceUtils.getConnection(dataSource)) {
            List<Department> departmentList = new ArrayList<>();

            String GET_ALL_DEPARTMENTS = "SELECT DEPARTMENT_ID, DEPARTMENT_NAME, MANAGER_ID, DEPARTMENTS.LOCATION_ID, CITY AS LOCATION " +
                    "FROM DEPARTMENTS INNER JOIN LOCATIONS ON DEPARTMENTS.LOCATION_ID = LOCATIONS.LOCATION_ID";

            ResultSet departmentsSet = connection.createStatement().executeQuery(GET_ALL_DEPARTMENTS);
            while (departmentsSet.next()) {
                departmentList.add(Department.builder()
                        .id(departmentsSet.getLong("DEPARTMENT_ID"))
                        .departmentName(departmentsSet.getString("DEPARTMENT_NAME"))
                        .managerId(departmentsSet.getLong("MANAGER_ID"))
                        .locationId(departmentsSet.getLong("LOCATION_ID"))
                        .location(departmentsSet.getString("LOCATION"))
                        .build());
            }
            return departmentList;
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

    @Override
    public Department save(Department department) {

        String ADD_DEPARTMENT_QUERY = "INSERT INTO DEPARTMENTS VALUES (DEPARTMENTS_SEQ.nextval, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(ADD_DEPARTMENT_QUERY)) {
            preparedStatement.setString(1, department.getDepartmentName());
            preparedStatement.setLong(2, department.getManagerId());
            preparedStatement.setLong(3, department.getLocationId());
            preparedStatement.execute();
            return department;
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            return null;
        }

    }

    @Override
    public Department findById(long id) {
        try (Connection connection = DataSourceUtils.getConnection(dataSource)) {

            String GET_DEPARTMENT_BY_ID = "SELECT DEPARTMENT_ID, DEPARTMENT_NAME, MANAGER_ID, DEPARTMENTS.LOCATION_ID, CITY AS LOCATION " +
                    "FROM DEPARTMENTS INNER JOIN LOCATIONS ON DEPARTMENTS.LOCATION_ID = LOCATIONS.LOCATION_ID" +
                    " WHERE DEPARTMENT_ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(GET_DEPARTMENT_BY_ID);
            preparedStatement.setLong(1, id);
            ResultSet departmentsSet = preparedStatement.executeQuery();

            if (departmentsSet.next()) {
                return Department.builder()
                        .id(departmentsSet.getLong("DEPARTMENT_ID"))
                        .departmentName(departmentsSet.getString("DEPARTMENT_NAME"))
                        .managerId(departmentsSet.getLong("MANAGER_ID"))
                        .locationId(departmentsSet.getLong("LOCATION_ID"))
                        .location(departmentsSet.getString("LOCATION"))
                        .build();
            } else {
                return null;
            }

        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

    @Override
    public Long deleteById(long id) {

        String DELETE_DEPARTMENT_QUERY = "DELETE FROM DEPARTMENTS WHERE DEPARTMENT_ID = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_DEPARTMENT_QUERY)) {

            preparedStatement.setLong(1, id);
            preparedStatement.execute();
            return id;
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            return -1L;
        }
    }

    @Override
    public Department updateById(long id, Department department) {

        department.setId(id);

        String UPDATE_BY_ID = "UPDATE DEPARTMENTS SET " +
                "DEPARTMENT_NAME = ?," +
                "MANAGER_ID = ?," +
                "LOCATION_ID = ? " +
                "WHERE DEPARTMENT_ID = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BY_ID)) {

            preparedStatement.setString(1, department.getDepartmentName());
            preparedStatement.setLong(2, department.getManagerId());
            preparedStatement.setLong(3, department.getLocationId());
            preparedStatement.setLong(4, id);
            preparedStatement.executeUpdate();

            return department;

        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }
}
