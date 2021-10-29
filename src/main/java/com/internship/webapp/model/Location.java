package com.internship.webapp.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@ToString
@Setter
@Getter
@Entity
@Table(name = "LOCATIONS")
@AllArgsConstructor
@NoArgsConstructor
//@JsonIgnoreProperties(value = {"locationId", "streetAddress", "postalCode", "stateProvince", "countryId"})
public class Location {

    @Id
    @Column(name = "location_id")
    private long locationId;

    @Column(name = "street_address")
    private String streetAddress;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "city")
    private String city;

    @Column(name = "state_province")
    private String stateProvince;

    @Column(name = "country_id")
    private String countryId;
}
