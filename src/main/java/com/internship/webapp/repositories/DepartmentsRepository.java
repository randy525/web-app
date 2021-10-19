package com.internship.webapp.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.internship.webapp.model.Department;
import com.internship.webapp.model.Employee;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Repository
public class DepartmentsRepository implements GenericRepository<Department> {

    private final ObjectWriter objectWriter;

    private final EntityManager entityManager;

    public DepartmentsRepository(DataSource dataSource, EntityManager entityManager) {
        this.entityManager = entityManager;
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Override
    public List<Department> findAll() {
        Session session = entityManager.unwrap(Session.class);
        final Query<Department> query = session.createQuery("FROM Department");
        final List<Department> resultList = query.getResultList();
        session.close();
        return resultList;
    }

    @Override
    public ResponseEntity<String> save(Department department) throws JsonProcessingException {
        Session session = entityManager.unwrap(Session.class);
        session.save(department);
        session.close();
        return ResponseEntity.status(HttpStatus.CREATED).body(objectWriter.writeValueAsString(department));
    }

    @Override
    public Department findById(long id) {
        return entityManager.find(Department.class, id);
    }

    @Override
    public String deleteById(long id) {
        Session session = entityManager.unwrap(Session.class);
        session.delete(entityManager.find(Department.class, id));
        session.close();
        return "Department with " + id + " deleted successfully!";
    }

    @Override
    public ResponseEntity<String> updateById(long id, Department department) throws JsonProcessingException {
        try {

            department.setId(id);

            Department departmentNew = entityManager.find(Department.class, id);

            departmentNew.setDepartmentName(department.getDepartmentName());
            departmentNew.setLocationId(department.getLocationId());
            departmentNew.setManagerId(department.getManagerId());
            departmentNew.setLocation(department.getLocation());

            Session session = entityManager.unwrap(Session.class);
            session.saveOrUpdate(departmentNew);
            session.close();

            /*CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaUpdate<Department> update = cb.createCriteriaUpdate(Department.class);

            Root<Department> departmentRoot = update.from(Department.class);

            update.set("departmentName", department.getDepartmentName());
            update.set("locationId", department.getLocationId());
            update.set("managerId", department.getManagerId());
            update.set("location", department.getLocation());

            update.where(cb.equal(departmentRoot.get("id"), id));
            entityManager.createQuery(update).executeUpdate();*/

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(objectWriter.writeValueAsString(departmentNew));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(exception.getMessage());
        }
    }
}
