package com.internship.webapp.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.webapp.model.Department;
import com.internship.webapp.model.Employee;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DepartmentsControllerIntegrationTest {

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
    void getDepartmentsFromDatabase() throws Exception {
        List<Department> expectedDepartments = List.of(
                new Department(10L, "IT", 0, 10, "Seattle"),
                new Department(20L, "Security", 1, 15, "London")
        );

        MvcResult result = mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expectedDepartments));

    }

    @Sql("init.sql")
    @Sql(scripts = "clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void whenAddDepartmentItInsertInDB() throws Exception {
        Department addedDepartment = new Department(30L, "LOL", 0, 15, "London");


        mockMvc.perform(post("/departments")
                .contentType("application/json")
                .content(objectMapper.disable(MapperFeature.USE_ANNOTATIONS).writeValueAsString(addedDepartment))
        ).andExpect(status().isCreated());

        String GET_DEPARTMENT_BY_ID = "SELECT DEPARTMENT_ID, DEPARTMENT_NAME, MANAGER_ID, DEPARTMENTS.LOCATION_ID, CITY AS LOCATION " +
                "FROM DEPARTMENTS INNER JOIN LOCATIONS USING (LOCATION_ID) WHERE DEPARTMENT_ID = ?";
        Department newDepartment = jdbcTemplate.queryForObject(GET_DEPARTMENT_BY_ID, new DepartmentRowMapper(), addedDepartment.getId());

        assertThat(newDepartment.equals(addedDepartment)).isTrue();
    }

    @Sql("init.sql")
    @Sql(scripts = "clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getDepartmentById() throws Exception {
        Department expectedDepartment = new Department(10L, "IT", 0, 10L, "Seattle");

        MvcResult result = mockMvc.perform(get("/departments/10"))
                .andExpect(status().isOk())
                .andReturn();

        Department resultDepartment = objectMapper.
                readerFor(Department.class)
                .readValue(result.getResponse().getContentAsString());

        assertThat(expectedDepartment.getDepartmentName().equals(resultDepartment.getDepartmentName())).isTrue();
        assertThat(expectedDepartment.getId() == resultDepartment.getId()).isTrue();
        assertThat(expectedDepartment.getLocation().equals(resultDepartment.getLocation())).isTrue();
    }

    @Sql("init.sql")
    @Sql(scripts = "clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void deleteDepartmentFromDatabase() throws Exception {

        mockMvc.perform(delete("/departments/20"))
                .andExpect(status().isOk());
        String countDepartmentsQuery = "SELECT COUNT(*) FROM DEPARTMENTS WHERE DEPARTMENT_ID = 20";

        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(countDepartmentsQuery);
            resultSet.next();
            int count = resultSet.getInt(1);
            assertThat(count).isEqualTo(0);
        }
    }

    @Sql("init.sql")
    @Sql(scripts = "clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updateDepartmentInDatabase() throws Exception {

        Department expectedDepartment = new Department(10L, "IT", 0, 10L, "Seattle");

        mockMvc.perform(put("/departments/10")
                .contentType("application/json")
                .content(objectMapper.disable(MapperFeature.USE_ANNOTATIONS).writeValueAsString(expectedDepartment))
        ).andExpect(status().isOk());
        String SELECT_DEPARTMENT_FIELDS = "SELECT DEPARTMENT_ID, DEPARTMENT_NAME, MANAGER_ID, DEPARTMENTS.LOCATION_ID, CITY AS LOCATION " +
                "FROM DEPARTMENTS INNER JOIN LOCATIONS ON DEPARTMENTS.LOCATION_ID = LOCATIONS.LOCATION_ID WHERE DEPARTMENT_ID = ?";
        Department newDepartment = jdbcTemplate.queryForObject(SELECT_DEPARTMENT_FIELDS, new DepartmentRowMapper(), expectedDepartment.getId());

        assertThat(newDepartment.equals(expectedDepartment)).isTrue();

    }
}

class DepartmentRowMapper implements RowMapper<Department> {

    @Override
    public Department mapRow(ResultSet rs, int rowNum) throws SQLException {
        if (rs.getString("DEPARTMENT_NAME") != null) {
            return Department.builder()
                    .id(rs.getLong("DEPARTMENT_ID"))
                    .departmentName(rs.getString("DEPARTMENT_NAME"))
                    .managerId(rs.getLong("MANAGER_ID"))
                    .locationId(rs.getLong("LOCATION_ID"))
                    .location(rs.getString("CITY"))
                    .build();
        } else {
            return null;
        }

    }
}
