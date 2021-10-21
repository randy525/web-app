package com.internship.webapp.repositories;

import com.internship.webapp.model.Employee;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EmployeeRepository implements GenericRepository<Employee> {

    @PersistenceContext
    private final EntityManager entityManager;

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
    public Employee save(Employee employee) {
        try {
            Session session = sessionFactory.openSession();
            session.getTransaction().begin();
            session.save(employee);
            session.flush();
            session.getTransaction().commit();
            return employee;
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

    @Override
    public Employee findById(long id) {

        return entityManager.find(Employee.class, id);

    }

    @Transactional
    @Override
    public Long deleteById(long id) {
        try {

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaDelete<Employee> cq = cb.createCriteriaDelete(Employee.class);

            Root<Employee> employeeRoot = cq.from(Employee.class);
            Predicate employeeIdPredicate = cb.equal(employeeRoot.get("id"), id);
            cq.where(employeeIdPredicate);

            entityManager.createQuery(cq).executeUpdate();

            return id;
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return -1L;
        }
    }

    @Transactional
    @Override
    public Employee updateById(long id, Employee employee) {
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

            return employee;
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }
}
