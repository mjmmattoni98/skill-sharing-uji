package com.aams.skillsharing.dao;

import com.aams.skillsharing.model.Offer;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class OfferRowMapper implements RowMapper<Offer> {
    @Override
    public Offer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Offer offer = new Offer();
        offer.setId(rs.getInt("id"));
        offer.setUser(rs.getString("user"));
        offer.setDescription(rs.getString("description"));
        offer.setStartDate(rs.getObject("start_date", LocalDate.class));
        offer.setFinishDate(rs.getObject("finish_date", LocalDate.class));

        return offer;
    }
}
