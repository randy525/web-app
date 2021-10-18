package com.internship.webapp.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.internship.webapp.model.Employee;
import lombok.RequiredArgsConstructor;
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
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmployeeRepository implements GenericRepository<Employee> {

    private final DataSource dataSource;

    @Override
    public List<Employee> findAll() {
        try(Connection connection = DataSourceUtils.getConnection(dataSource)) {
            List<Employee> employeeList = new ArrayList<>();
            String query = "SELECT * FROM employees";
            ResultSet employees = connection.createStatement().executeQuery(query);
            while(employees.next()) {
                employeeList.add(Employee.builder()
                        .id(employees.getLong("employee_id"))
                        .firstName(employees.getString("first_name"))
                        .lastName(employees.getString("last_name"))
                        .email(employees.getString("email"))
                        .phoneNumber(employees.getString("phone_number"))
                        .hireDate(employees.getDate("hire_date"))
                        .jobId(employees.getString("job_id"))
                        .salary(employees.getDouble("salary"))
                        .commissionPct(employees.getDouble("commission_pct"))
                        .managerId(employees.getLong("manager_id"))
                        .departmentId(employees.getLong("department_id"))
                        .build());
            }
            return employeeList;
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<String> save(Employee employee) throws JsonProcessingException{
        String query = "INSERT INTO EMPLOYEES VALUES (employees_seq.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        try(Connection connection = DataSourceUtils.getConnection(dataSource);
            PreparedStatement preparedStatement = connection.prepareStatement(query);) {

            preparedStatement.setString(1, employee.getFirstName());
            preparedStatement.setString(2, employee.getLastName());
            preparedStatement.setString(3, employee.getEmail());
            preparedStatement.setString(4, employee.getPhoneNumber());
            preparedStatement.setDate(5, employee.getHireDate());
            preparedStatement.setString(6, employee.getJobId());
            preparedStatement.setDouble(7, employee.getSalary());
            preparedStatement.setDouble(8, employee.getCommissionPct());
            preparedStatement.setLong(9, employee.getManagerId());
            preparedStatement.setLong(10, employee.getDepartmentId());
            preparedStatement.execute();

            return ResponseEntity.status(HttpStatus.CREATED).body(ow.writeValueAsString(employee));
        } catch (SQLException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
    }

    @Override
    public Employee findById(long id) {
        return findAll().stream()
                .filter(employee -> employee.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String deleteById(long id) {
        String query = "DELETE FROM EMPLOYEES WHERE employee_id = ?";
        try(Connection connection = DataSourceUtils.getConnection(dataSource);
            PreparedStatement preparedStatement = connection.prepareStatement(query);) {

            preparedStatement.setLong(1, id);
            preparedStatement.execute();
            return "Deleted!";
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            return "Error!";
        }
    }

    @Override
    public ResponseEntity<String> updateById(long id, Employee employee) throws JsonProcessingException{
        String query = "UPDATE EMPLOYEES SET " +
                "first_name = ?," +
                "last_name = ?," +
                "email = ?," +
                "phone_number = ?," +
                "hire_date = ?," +
                "job_id = ?," +
                "salary = ?," +
                "commission_pct = ?," +
                "manager_id = ?," +
                "department_id = ?" +
                "WHERE employee_id = ?";
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);) {

            employee.setId(id);

            preparedStatement.setString(1, employee.getFirstName());
            preparedStatement.setString(2, employee.getLastName());
            preparedStatement.setString(3, employee.getEmail());
            preparedStatement.setString(4, employee.getPhoneNumber());
            preparedStatement.setDate(5, employee.getHireDate());
            preparedStatement.setString(6, employee.getJobId());
            preparedStatement.setDouble(7, employee.getSalary());
            preparedStatement.setDouble(8, employee.getCommissionPct());
            preparedStatement.setLong(9, employee.getManagerId());
            preparedStatement.setLong(10, employee.getDepartmentId());
            preparedStatement.setLong(11, id);
            preparedStatement.execute();

            return ResponseEntity.status(HttpStatus.OK).body(ow.writeValueAsString(employee));
        } catch (SQLException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
    }
}
