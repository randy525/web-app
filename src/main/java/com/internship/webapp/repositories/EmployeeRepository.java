package com.internship.webapp.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.internship.webapp.model.Employee;
import lombok.RequiredArgsConstructor;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EmployeeRepository implements GenericRepository<Employee> {

    private final DataSource dataSource;

    @PersistenceContext
    private final EntityManager entityManager;

    private final ObjectMapper objectMapper;

    private final SessionFactory sessionFactory;

    @Override
    public List<Employee> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        cq.from(Employee.class);
        TypedQuery<Employee> queryForAllEmployee = entityManager.createQuery(cq);
        return queryForAllEmployee.getResultList();
    }


    @Transactional
    @Override
    public ResponseEntity<String> save(Employee employee) throws JsonProcessingException {
        try {
            Session session = sessionFactory.openSession();
            session.getTransaction().begin();
            session.save(employee);
            session.flush();
            session.getTransaction().commit();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(employee));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(exception.getMessage());
        }
    }

    @Override
    public Employee findById(long id) {

        return entityManager.find(Employee.class, id);

        /*CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);

        Root<Employee> employeeRoot = cq.from(Employee.class);
        Predicate employeeIdPredicate = cb.equal(employeeRoot.get("id"), id);
        cq.where(employeeIdPredicate);

        TypedQuery<Employee> queryForEmployee = entityManager.createQuery(cq);

        return queryForEmployee.getResultList()
                .stream()
                .findFirst()
                .orElse(null);*/

    }

    @Transactional
    @Override
    public String deleteById(long id) {
        try {

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaDelete<Employee> cq = cb.createCriteriaDelete(Employee.class);

            Root<Employee> employeeRoot = cq.from(Employee.class);
            Predicate employeeIdPredicate = cb.equal(employeeRoot.get("id"), id);
            cq.where(employeeIdPredicate);

            int rowDeleted = entityManager.createQuery(cq).executeUpdate();

            return "Deleted " + rowDeleted + " row!";
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return "Error: " + exception.getMessage();
        }
    }

    @Transactional
    @Override
    public ResponseEntity<String> updateById(long id, Employee employee) throws JsonProcessingException {
        try {
            employee.setId(id);
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaUpdate<Employee> update = cb.createCriteriaUpdate(Employee.class);

            Root<Employee> employeeRoot = update.from(Employee.class);

            update.set("firstName", employee.getFirstName());
            update.set("lastName", employee.getLastName());
            update.set("email", employee.getEmail());
            update.set("phoneNumber", employee.getPhoneNumber());
            update.set("departmentId", employee.getDepartmentId());
            update.set("hireDate", employee.getHireDate());
            update.set("jobId", employee.getJobId());
            update.set("salary", employee.getSalary());
            update.set("commissionPct", employee.getCommissionPct());
            update.set("managerId", employee.getManagerId());
            update.where(cb.equal(employeeRoot.get("id"), id));
            entityManager.createQuery(update).executeUpdate();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(employee));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(exception.getMessage());
        }
    }
}
