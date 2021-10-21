package com.internship.webapp.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.webapp.model.Employee;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import javax.sql.DataSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeesControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void init() {
        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
    }

    @Sql("init.sql")
    @Sql(scripts = "clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getAllEmployeesFromDatabase() throws Exception {

        List<Employee> expectedEmployes = List.of(
                new Employee(0L,
                        "Alexey",
                        "Neikulov",
                        10,
                        "alex.neikulov@gmail.com",
                        "060748612",
                        Date.valueOf(LocalDate.of(2021, 10, 17)),
                        "IT_PROG",
                        17000,
                        0,
                        100),
                new Employee(1L,
                        "John",
                        "Wick",
                        10,
                        "dog.wick@gmail.com",
                        "060748481",
                        Date.valueOf(LocalDate.of(2021, 10, 16)),
                        "IT_PROG",
                        15000,
                        0,
                        100)
        );

        MvcResult result = mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expectedEmployes));

    }

    @Sql("init.sql")
    @Sql(scripts = "clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void ifAddEmployeeItInsertedInDatabase() throws Exception {

        Employee addedEmployee = new Employee(3L,
                "Kagami",
                "Taiga",
                10,
                "kagami.taiga@gmail.com",
                "060666777",
                Date.valueOf(LocalDate.of(2020, 3, 2)),
                "SOME_JOB",
                19000,
                0,
                60);


        mockMvc.perform(post("/employees")
                .contentType("application/json")
                .content(objectMapper.disable(MapperFeature.USE_ANNOTATIONS).writeValueAsString(addedEmployee))
                ).andExpect(status().isCreated());

        String GET_EMPLOYEE_BY_ID = "SELECT * FROM EMPLOYEES WHERE EMPLOYEE_ID = ?";
        Employee newEmployee = jdbcTemplate.queryForObject(GET_EMPLOYEE_BY_ID, new CustomerRowMapper(), addedEmployee.getId());

        assertThat(newEmployee).isEqualTo(addedEmployee);

    }

    @Sql("init.sql")
    @Sql(scripts = "clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getEmployeeByIdFromDatabase() throws Exception {
        Employee expectedEmployee = new Employee(0L,
                        "Alexey",
                        "Neikulov",
                        10,
                        "alex.neikulov@gmail.com",
                        "060748612",
                        Date.valueOf(LocalDate.of(2021, 10, 17)),
                        "IT_PROG",
                        17000,
                        0,
                        100);



        MvcResult result = mockMvc.perform(get("/employees/0"))
                .andExpect(status().isOk())
                .andReturn();

        Employee resultEmployee = objectMapper.
                readerFor(Employee.class)
                .readValue(result.getResponse().getContentAsString());

        assertThat(expectedEmployee.equals(resultEmployee)).isTrue();
    }


    @Sql("init.sql")
    @Sql(scripts = "clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void deleteEmployeeFromDatabase() throws Exception {
        mockMvc.perform(delete("/employees/1"))
                .andExpect(status().isOk());
        String COUNT_BY_ID = "SELECT COUNT(EMPLOYEE_ID) FROM EMPLOYEES WHERE EMPLOYEE_ID = 1";

        try(Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(COUNT_BY_ID);
            resultSet.next();
            int count = resultSet.getInt(1);
            assertThat(count).isEqualTo(0);
        }
    }

    @Sql("init.sql")
    @Sql(scripts = "clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void ifUpdateEmployeeItUpdatedInDatabase() throws Exception {

        Employee updatedEmployee = new Employee(0L,
                "Kagami",
                "Taiga",
                10,
                "kagami.taiga@gmail.com",
                "060666777",
                Date.valueOf(LocalDate.of(2020, 3, 2)),
                "SOME_JOB",
                19000,
                0,
                60);


        mockMvc.perform(put("/employees/0")
                .contentType("application/json")
                .content(objectMapper.disable(MapperFeature.USE_ANNOTATIONS).writeValueAsString(updatedEmployee))
        ).andExpect(status().isOk());

        String GET_EMPLOYEE_BY_ID = "SELECT * FROM EMPLOYEES WHERE EMPLOYEE_ID = ?";
        Employee newEmployee = jdbcTemplate.queryForObject(GET_EMPLOYEE_BY_ID, new CustomerRowMapper(), updatedEmployee.getId());

        assertThat(newEmployee.equals(updatedEmployee)).isTrue();
    }
}

class CustomerRowMapper implements RowMapper<Employee> {

    @Override
    public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
        if(rs.getString("first_name") != null) {
            Employee employee = Employee.builder()
                    .id(rs.getLong("employee_id"))
                    .firstName(rs.getString("first_name"))
                    .lastName(rs.getString("last_name"))
                    .email(rs.getString("email"))
                    .phoneNumber(rs.getString("phone_number"))
                    .hireDate(rs.getDate("hire_date"))
                    .jobId(rs.getString("job_id"))
                    .salary(rs.getDouble("salary"))
                    .commissionPct(rs.getDouble("commission_pct"))
                    .managerId(rs.getLong("manager_id"))
                    .departmentId(rs.getLong("department_id"))
                    .build();
            return employee;
        }

        return null;
    }
}