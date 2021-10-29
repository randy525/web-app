package com.internship.webapp.controllers;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.webapp.model.Department;
import com.internship.webapp.model.Location;
import com.internship.webapp.servicies.DepartmentsService;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DepartmentsControllerIntegrationTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final SessionFactory sessionFactory;
    private final DepartmentsService departmentsService;

    @Autowired
    public DepartmentsControllerIntegrationTest(MockMvc mockMvc,
                                                ObjectMapper objectMapper,
                                                SessionFactory sessionFactory,
                                                DepartmentsService departmentsService) {

        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.sessionFactory = sessionFactory;
        this.departmentsService = departmentsService;
    }

    private final static Location location1 = new Location(1800L, "147 Spadina Ave", "M5V 2L7", "Toronto", "Ontario", "CA");
    private final static Location location2 = new Location(1700L, "2004 Charade Rd", "98199", "Seattle", "Washington", "US");

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Commit
    @BeforeEach
    void initDepartments() {
        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);

        Session session = sessionFactory.openSession();

        session.getTransaction().begin();
        session.replicate(location1, ReplicationMode.IGNORE);
        session.replicate(location2, ReplicationMode.IGNORE);
        session.getTransaction().commit();

        session.close();
    }


    @Test
    void getDepartments() throws Exception {

        Department department1 = new Department(1L, "Administration", 200L, 1700L, location2);
        Department department2 = new Department(2L, "Marketing", 201L, 1800L, location1);

        departmentsService.save(department1);
        departmentsService.save(department2);

        List<Department> expectedDepartments = departmentsService.findAll();

        MvcResult result = mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .isEqualTo(objectMapper.writeValueAsString(expectedDepartments));
    }

    @Test
    void addDepartment() throws Exception {

        Department newDepartment = new Department(1L, "Administration", 205L, 1700L, location2);

        MvcResult result = mockMvc.perform(post("/departments")
                        .contentType("application/json")
                        .content(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(newDepartment)))
                .andExpect(status().isOk())
                .andReturn();

        Department actualDepartment = objectMapper.readValue(result.getResponse().getContentAsString(), Department.class);
        newDepartment.setId(actualDepartment.getId());

        assertThat(objectMapper.writeValueAsString(actualDepartment))
                .isEqualTo(objectMapper.writeValueAsString(newDepartment));
    }


    @Test
    void getDepartmentById() throws Exception {

        Department department = new Department(1L, "Name1", 200L, 1700L, location2);

        department = departmentsService.save(department);

        MvcResult result = mockMvc.perform(get("/departments/" + department.getId()))
                .andExpect(status().isOk())
                .andReturn();

        Department resultDepartment = objectMapper.
                readerFor(Department.class)
                .readValue(result.getResponse().getContentAsString());

        assertThat(objectMapper.writeValueAsString(resultDepartment))
                .isEqualTo(objectMapper.writeValueAsString(resultDepartment));
    }

    @Test
    void deleteDepartment() throws Exception {

        Department department = new Department(1L, "Name2", 200L, 1700L, location2);

        departmentsService.save(department);

        mockMvc.perform(delete("/departments/" + department.getId()))
                .andExpect(status().isOk());

        Department deletedDepartment = departmentsService.findById(department.getId());

        assertThat(deletedDepartment).isNull();
    }

    @Test
    void updateDepartment() throws Exception {
        Department newDepartment = new Department(1L, "Finance", 250L, 1700L, location2);

        newDepartment = departmentsService.save(newDepartment);


        mockMvc.perform(put("/departments/" + newDepartment.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newDepartment)))
                .andExpect(status().isOk());

        Department actualDepartment = departmentsService.findById(newDepartment.getId());

        assertThat(objectMapper.writeValueAsString(actualDepartment))
                .isEqualTo(objectMapper.writeValueAsString(newDepartment));

    }
}
