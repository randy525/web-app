package com.internship.webapp.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"managerId", "locationId"}, allowSetters = true)
public class Department {

    private long id;

    @NotNull(message = "Department name must not be null")
    @NotEmpty(message = "Department name must not be empty")
    @NotBlank(message = "Department name must not be blank")
    private String departmentName;
    private long managerId;
    private long locationId;

    @NotNull(message = "Location must not be null")
    @NotEmpty(message = "Location must not be empty")
    @NotBlank(message = "Location must not be blank")
    private String location;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

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
