package com.internship.webapp.controllers;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.webapp.model.Employee;
import com.internship.webapp.repositories.EmployeeRepository;
import com.internship.webapp.servicies.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeesControllerIntegrationTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final EmployeeService employeeService;

    @Autowired
    public EmployeesControllerIntegrationTest(MockMvc mockMvc,
                                              ObjectMapper objectMapper,
                                              EmployeeService employeeService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.employeeService = employeeService;
    }

    @BeforeEach
    void initTable() {
        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
    }


    @Test
    void getEmployees() throws Exception {

        List<Employee> expectedEmployees = List.of(
                new Employee(1L,
                        "Alexey",
                        "Neikulov",
                        10L,
                        "alex.neikulov@gmail.com",
                        "060748612",
                        Date.valueOf(LocalDate.of(2021, 10, 17)),
                        "IT_PROG",
                        17000,
                        null,
                        100L),
                new Employee(2L,
                        "John",
                        "Wick",
                        10L,
                        "dog.wick@gmail.com",
                        "060748481",
                        Date.valueOf(LocalDate.of(2021, 10, 16)),
                        "IT_PROG",
                        15000,
                        null,
                        100L)
        );

        employeeService.save(expectedEmployees.get(0));
        employeeService.save(expectedEmployees.get(1));

        expectedEmployees = employeeService.findAll();

        MvcResult result = mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expectedEmployees));

    }

    @Test
    void addEmployee() throws Exception {

        Employee addedEmployee = new Employee(1L,
                "Kagami",
                "Taiga",
                10L,
                "kagami.taiga@gmail.com",
                "060666777",
                Date.valueOf(LocalDate.of(2020, 3, 2)),
                "SOME_JOB",
                19000,
                0D,
                60L);


        MvcResult result = mockMvc.perform(post("/employees")
                        .contentType("application/json")
                        .content(objectMapper.disable(MapperFeature.USE_ANNOTATIONS)
                                .writeValueAsString(addedEmployee)))
                .andExpect(status().isOk())
                .andReturn();

        Employee newEmployee = objectMapper.readValue(result.getResponse().getContentAsString(), Employee.class);
        addedEmployee.setId(newEmployee.getId());

        assertThat(newEmployee).isEqualTo(addedEmployee);

    }

    @Test
    void getEmployeeById() throws Exception {
        Employee expectedEmployee = new Employee(1L,
                "Vasilii",
                "Pupkin",
                10L,
                "pup.vasin@gmail.com",
                "060748696",
                Date.valueOf(LocalDate.of(2021, 10, 17)),
                "IT_PROG",
                17000,
                0D,
                100L);
        expectedEmployee = employeeService.save(expectedEmployee);

        MvcResult result = mockMvc.perform(get("/employees/" + expectedEmployee.getId()))
                .andExpect(status().isOk())
                .andReturn();

        Employee resultEmployee = objectMapper.
                readerFor(Employee.class)
                .readValue(result.getResponse().getContentAsString());

        assertThat(expectedEmployee.equals(resultEmployee)).isTrue();
    }

    @Test
    void deleteEmployee() throws Exception {

        Employee expectedEmployee = new Employee(1L,
                "Dmitrii",
                "Jones",
                10L,
                "jackn@gmail.com",
                "060615010",
                Date.valueOf(LocalDate.of(2020, 8, 11)),
                "IT_PROG",
                17000,
                0D,
                100L);
        expectedEmployee = employeeService.save(expectedEmployee);

        mockMvc.perform(delete("/employees/" + expectedEmployee.getId()))
                .andExpect(status().isOk());

        Employee deletedEmployee = employeeService.findById(expectedEmployee.getId());

        assertThat(deletedEmployee).isNull();
    }

    @Test
    void updateEmployee() throws Exception {

        Employee addingEmployee = new Employee(1L,
                "Kagami",
                "Taiga",
                10L,
                "kagami@gmail.com",
                "060666999",
                Date.valueOf(LocalDate.of(2020, 3, 2)),
                "SOME_JOB",
                19000,
                0D,
                60L);

        addingEmployee = employeeService.save(addingEmployee);

        Employee updatedEmployee = new Employee(1L,
                "Tetsu",
                "Kuroko",
                10L,
                "kuroko@gmail.com",
                "789456123",
                Date.valueOf(LocalDate.of(2020, 3, 2)),
                "SOME_JOB",
                1900,
                0D,
                60L);
        updatedEmployee.setId(addingEmployee.getId());


        mockMvc.perform(put("/employees/" + updatedEmployee.getId())
                .contentType("application/json")
                .content(objectMapper.disable(MapperFeature.USE_ANNOTATIONS).writeValueAsString(updatedEmployee))
        ).andExpect(status().isOk());

        Employee actualEmployee = employeeService.findById(updatedEmployee.getId());

        assertThat(objectMapper.writeValueAsString(updatedEmployee))
                .isEqualTo(objectMapper.writeValueAsString(actualEmployee));
    }
}