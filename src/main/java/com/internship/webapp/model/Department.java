package com.internship.webapp.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
//@JsonIgnoreProperties(value = {"managerId", "locationId"}, allowSetters = true)
@Entity
@Table(name = "DEPARTMENTS")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "departments_seq")
    @SequenceGenerator(name="departments_seq",
            sequenceName="DEPARTMENTS_SEQ", allocationSize = 1)
    @Column(name = "DEPARTMENT_ID")
    private long id;

    @NotNull(message = "Department name must not be null")
    @NotEmpty(message = "Department name must not be empty")
    @NotBlank(message = "Department name must not be blank")
    @Column(name = "DEPARTMENT_NAME")
    private String departmentName;

    @Column(name = "MANAGER_ID")
    private Long managerId;

    @Column(name = "LOCATION_ID", insertable = false, updatable = false)
    private Long locationId;

    @NotNull(message = "Department location must not be null")
    @ManyToOne
    @JoinColumn(name = "LOCATION_ID")
    private Location location;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return id == that.id &&
                locationId == that.locationId &&
                departmentName.equals(that.departmentName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, departmentName, locationId);
    }
}
