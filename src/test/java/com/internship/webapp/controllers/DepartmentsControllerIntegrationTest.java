package com.internship.webapp.controllers;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.webapp.model.Department;
import com.internship.webapp.model.Location;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DepartmentsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private EntityManager entityManager;

    private Location location1;
    private Location location2;
    private Department department1;
    private Department department2;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Commit
    void initLocations() {
        location1 = new Location(1800L, "147 Spadina Ave", "M5V 2L7", "Toronto", "Ontario", "CA");
        location2 = new Location(1700L, "2004 Charade Rd", "98199", "Seattle", "Washington", "US");

        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        session.save(location1);
        session.save(location2);
        session.flush();
        session.getTransaction().commit();
        session.close();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Commit
    void initDepartments() {
        department1 = new Department(1L, "Administration", 200L, 1700L, location2);
        department2 = new Department(2L, "Marketing", 201L, 1800L, location1);

        Session session = sessionFactory.openSession();

        session.getTransaction().begin();
        session.save(department1);
        session.save(department2);
        session.flush();
        session.getTransaction().commit();
        session.close();
    }

    @BeforeAll
    void insertData() {
        initLocations();
        initDepartments();
    }

    @BeforeEach
    void init() {
        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
    }


    @Order(1)
    @Test
    void getDepartments() throws Exception {

        List<Department> expectedDepartments = List.of(department1, department2);

        MvcResult result = mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .isEqualTo(objectMapper.writeValueAsString(expectedDepartments));
    }

    @Order(3)
    @Test
    void addDepartment() throws Exception {
        Department newDepartment = new Department(3L, "Administration", 205L, 1700L, location2);

        mockMvc.perform(post("/departments")
                        .contentType("application/json")
                        .content(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(newDepartment)))
                .andExpect(status().isCreated());

        Department actualDepartment = entityManager.find(Department.class, 3L);

        assertThat(objectMapper.writeValueAsString(actualDepartment))
                .isEqualTo(objectMapper.writeValueAsString(newDepartment));
    }


    @Order(2)
    @Test
    void getDepartmentById() throws Exception {

        MvcResult result = mockMvc.perform(get("/departments/1"))
                .andExpect(status().isOk())
                .andReturn();

        Department resultDepartment = objectMapper.
                readerFor(Department.class)
                .readValue(result.getResponse().getContentAsString());

        assertThat(objectMapper.writeValueAsString(resultDepartment))
                .isEqualTo(objectMapper.writeValueAsString(department1));
    }

    @Order(5)
    @Test
    void deleteDepartment() throws Exception {
        mockMvc.perform(delete("/departments/2"))
                .andExpect(status().isOk());

        Department deletedDepartment = entityManager.find(Department.class, 2L);

        assertThat(deletedDepartment).isNull();
    }

    @Order(4)
    @Test
    void updateDepartment() throws Exception {
        Department newDepartment = new Department(1L, "Finance", 250L, 1700L, location2);

        mockMvc.perform(put("/departments/1")
                        .contentType("application/json")
                        .content(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(newDepartment)))
                .andExpect(status().isCreated());

        Department actualDepartment = entityManager.find(Department.class, 1L);

        assertThat(objectMapper.writeValueAsString(actualDepartment))
                .isEqualTo(objectMapper.writeValueAsString(newDepartment));

    }
}