package com.internship.webapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.internship.webapp.validation.UniqueField;
import lombok.*;

import javax.validation.constraints.*;
import java.sql.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"hireDate", "jobId", "commissionPct", "managerId"}, allowSetters = true)
public class Employee {

    private long id;

    @NotNull(message = "First name must not be null")
    @NotEmpty(message = "First name must not be empty")
    @NotBlank(message = "First name must not be blank")
    private String firstName;

    @NotNull(message = "Last name must not be null")
    @NotEmpty(message = "Last name must not be empty")
    @NotBlank(message = "Last name must not be blank")
    private String lastName;
    private long departmentId;

    @NotNull(message = "Email must not be null")
    @NotEmpty(message = "Email must not be empty")
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be valid")
    @UniqueField(message = "The email must be unique")
    private String email;

    @NotNull(message = "Phone number must not be null")
    @NotEmpty(message = "Phone number must not be empty")
    @NotBlank(message = "Phone number must not be blank")
    @UniqueField(message = "The phone number must be unique")
    private String phoneNumber;
    private Date hireDate;
    private String jobId;

    @DecimalMin(value = "1.0", message = "The salary must be greater than or equal to 1")
    private double salary;
    private double commissionPct;
    private long managerId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
