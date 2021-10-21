package com.internship.webapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.internship.webapp.validation.UniqueField;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.Constraint;
import javax.validation.constraints.*;
import java.sql.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"hireDate", "jobId", "commissionPct", "managerId"}, allowSetters = true)
@Entity
@Table(name = "EMPLOYEES")
public class Employee {

    @Id
    @Column(name = "EMPLOYEE_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employees_seq")
    @SequenceGenerator(name="employees_seq",
            sequenceName="EMPLOYEES_SEQ", allocationSize = 1)
    private long id;

    @NotNull(message = "First name must not be null")
    @NotEmpty(message = "First name must not be empty")
    @NotBlank(message = "First name must not be blank")
    @Column(name = "FIRST_NAME")
    private String firstName;

    @NotNull(message = "Last name must not be null")
    @NotEmpty(message = "Last name must not be empty")
    @NotBlank(message = "Last name must not be blank")
    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "DEPARTMENT_ID")
    private Long departmentId;

    @NotNull(message = "Email must not be null")
    @NotEmpty(message = "Email must not be empty")
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be valid")
    @Column(name = "EMAIL", unique = true)
    private String email;

    @NotNull(message = "Phone number must not be null")
    @NotEmpty(message = "Phone number must not be empty")
    @NotBlank(message = "Phone number must not be blank")
    @Column(name = "PHONE_NUMBER", unique = true)
    private String phoneNumber;

    @Column(name = "HIRE_DATE")
    private Date hireDate;

    @Column(name = "JOB_ID")
    private String jobId;

    @DecimalMin(value = "1.0", message = "The salary must be greater than or equal to 1")
    @Column(name = "SALARY")
    private double salary;

    @Column(name = "COMMISSION_PCT")
    private Double commissionPct;

    @Column(name = "MANAGER_ID")
    private Long managerId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id &&
                firstName.equals(employee.firstName) &&
                lastName.equals(employee.lastName) &&
                email.equals(employee.email) &&
                phoneNumber.equals(employee.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, phoneNumber);
    }

}
