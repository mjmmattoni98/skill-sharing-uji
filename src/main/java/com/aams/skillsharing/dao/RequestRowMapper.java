package com.aams.skillsharing.dao;

import com.aams.skillsharing.model.Request;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class RequestRowMapper implements RowMapper<Request> {

    @Override
    public Request mapRow(ResultSet rs, int rowNum) throws SQLException {
        Request request = new Request();
        request.setId(rs.getInt("id"));
        request.setUsername(rs.getString("username"));
        request.setName(rs.getString("name"));
        request.setDescription(rs.getString("description"));
        request.setStartDate(rs.getObject("start_date", LocalDate.class));
        request.setFinishDate(rs.getObject("finish_date", LocalDate.class));

        return request;
    }
}
