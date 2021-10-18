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
    private final ObjectWriter objectWriter;

    public DepartmentsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Override
    public List<Department> findAll() {
        try (Connection connection = DataSourceUtils.getConnection(dataSource)) {
            List<Department> departmentList = new ArrayList<>();
            String query = "SELECT DEPARTMENT_ID, DEPARTMENT_NAME, MANAGER_ID, DEPARTMENTS.LOCATION_ID, CITY AS LOCATION " +
                    "FROM DEPARTMENTS INNER JOIN LOCATIONS ON DEPARTMENTS.LOCATION_ID = LOCATIONS.LOCATION_ID";
            ResultSet departmentsSet = connection.createStatement().executeQuery(query);
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
    public ResponseEntity<String> save(Department department) throws JsonProcessingException {
        String query = "SELECT LOCATION_ID FROM LOCATIONS WHERE CITY = ?";
        long locationId;

        try (Connection connection = DataSourceUtils.getConnection(dataSource)) {

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, department.getLocation());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    locationId = resultSet.getLong(1);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Not found the " + department.getLocation() + " location in database");
                }
            }

            query = "INSERT INTO DEPARTMENTS VALUES (DEPARTMENTS_SEQ.nextval, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, department.getDepartmentName());
                preparedStatement.setLong(2, department.getManagerId());
                preparedStatement.setLong(3, locationId);
                preparedStatement.execute();
                return ResponseEntity.status(HttpStatus.CREATED).body(objectWriter.writeValueAsString(department));
            }
        } catch (SQLException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
    }

    @Override
    public Department findById(long id) {
        return findAll().stream()
                .filter(department -> department.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String deleteById(long id) {
        String query = "DELETE FROM DEPARTMENTS WHERE DEPARTMENT_ID = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
            return "Deleted!";
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            return "Error!";
        }
    }

    @Override
    public ResponseEntity<String> updateById(long id, Department department) throws JsonProcessingException {
        String query = "SELECT LOCATION_ID FROM LOCATIONS WHERE CITY = ?";
        long locationId;

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, department.getLocation());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    locationId = resultSet.getLong(1);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Not found the " + department.getLocation() + " location in database");
                }
            }

            query = "UPDATE DEPARTMENTS SET " +
                    "DEPARTMENT_NAME = ?," +
                    "MANAGER_ID = ?," +
                    "LOCATION_ID = ? " +
                    "WHERE DEPARTMENT_ID = ?";
            department.setId(id);

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, department.getDepartmentName());
                preparedStatement.setLong(2, department.getManagerId());
                preparedStatement.setLong(3, locationId);
                preparedStatement.setLong(4, id);
                preparedStatement.executeUpdate();

                return ResponseEntity.status(HttpStatus.OK).body(objectWriter.writeValueAsString(department));
            }

        } catch (SQLException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
    }
}
