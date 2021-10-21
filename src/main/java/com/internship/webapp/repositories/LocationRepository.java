package com.internship.webapp.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@RequiredArgsConstructor
public class LocationRepository {

    private final DataSource dataSource;

    public Long getIdByCity(String city) {
        String GET_ID_QUERY = "SELECT LOCATION_ID FROM LOCATIONS WHERE CITY = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ID_QUERY)) {
            preparedStatement.setString(1, city);

            ResultSet set = preparedStatement.executeQuery();
            if (set.next()) {
                return set.getLong(1);
            } else {
                return null;
            }

        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }
}
