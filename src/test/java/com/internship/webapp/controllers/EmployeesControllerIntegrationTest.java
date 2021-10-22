package com.internship.webapp.controllers;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.webapp.model.Employee;
import lombok.RequiredArgsConstructor;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.persistence.EntityManager;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private SessionFactory sessionFactory;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void initTable() {
        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
        Employee employee1 = new Employee(1L,
                "Alexey",
                "Neikulov",
                10L,
                "alex.neikulov@gmail.com",
                "060748612",
                Date.valueOf(LocalDate.of(2021, 10, 17)),
                "IT_PROG",
                17000,
                null,
                100L);
        Employee employee2 = new Employee(2L,
                "John",
                "Wick",
                10L,
                "dog.wick@gmail.com",
                "060748481",
                Date.valueOf(LocalDate.of(2021, 10, 16)),
                "IT_PROG",
                15000,
                null,
                100L);

        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        session.replicate(employee1, ReplicationMode.IGNORE);
        session.replicate(employee2, ReplicationMode.IGNORE);
        session.getTransaction().commit();
        session.close();
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

        MvcResult result = mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expectedEmployees));

    }

    @Test
    void addEmployee() throws Exception {

        Session session = sessionFactory.openSession();

        session.getTransaction().begin();
        session.delete(session.get(Employee.class, 1L));
        session.delete(session.get(Employee.class, 2L));
        session.getTransaction().commit();
        session.close();

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


        mockMvc.perform(post("/employees")
                .contentType("application/json")
                .content(objectMapper.disable(MapperFeature.USE_ANNOTATIONS).writeValueAsString(addedEmployee))
        ).andExpect(status().isCreated());

        Employee newEmployee = entityManager.find(Employee.class, addedEmployee.getId());

        assertThat(newEmployee).isEqualTo(addedEmployee);

    }

    @Test
    void getEmployeeById() throws Exception {
        Employee expectedEmployee = new Employee(1L,
                "Alexey",
                "Neikulov",
                10L,
                "alex.neikulov@gmail.com",
                "060748612",
                Date.valueOf(LocalDate.of(2021, 10, 17)),
                "IT_PROG",
                17000,
                0D,
                100L);


        MvcResult result = mockMvc.perform(get("/employees/1"))
                .andExpect(status().isOk())
                .andReturn();

        Employee resultEmployee = objectMapper.
                readerFor(Employee.class)
                .readValue(result.getResponse().getContentAsString());

        assertThat(expectedEmployee.equals(resultEmployee)).isTrue();
    }

    @Test
    void deleteEmployee() throws Exception {
        mockMvc.perform(delete("/employees/2"))
                .andExpect(status().isOk());

        Employee deletedEmployee = entityManager.find(Employee.class, 2L);

        assertThat(deletedEmployee).isNull();
    }

    @Test
    void updateEmployee() throws Exception {

        Employee updatedEmployee = new Employee(1L,
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


        mockMvc.perform(put("/employees/1")
                .contentType("application/json")
                .content(objectMapper.disable(MapperFeature.USE_ANNOTATIONS).writeValueAsString(updatedEmployee))
        ).andExpect(status().isCreated());

        Employee actualEmployee = entityManager.find(Employee.class, updatedEmployee.getId());

        assertThat(actualEmployee).isEqualTo(updatedEmployee);
    }
}