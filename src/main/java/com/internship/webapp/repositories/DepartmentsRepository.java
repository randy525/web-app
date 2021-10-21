package com.internship.webapp.repositories;

import com.internship.webapp.model.Department;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Repository
public class DepartmentsRepository implements GenericRepository<Department> {

    private final EntityManager entityManager;

    @Override
    public List<Department> findAll() {
        Session session = entityManager.unwrap(Session.class);
        final Query<Department> query = session.createQuery("FROM Department");
        final List<Department> resultList = query.getResultList();
        session.close();
        return resultList;
    }

    @Override
    public Department save(Department department){
        Session session = entityManager.unwrap(Session.class);
        session.save(department);
        session.close();
        return department;
    }

    @Override
    public Department findById(long id) {
        return entityManager.find(Department.class, id);
    }

    @Override
    public Long deleteById(long id) {
        Session session = entityManager.unwrap(Session.class);
        session.delete(entityManager.find(Department.class, id));
        session.close();
        return id;
    }

    @Override
    public Department updateById(long id, Department department) {
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

            return department;
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }
}
