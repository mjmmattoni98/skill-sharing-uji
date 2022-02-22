package com.aams.skillsharing.dao;

import com.aams.skillsharing.model.Email;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class EmailRowMapper implements RowMapper<Email> {
    @Override
    public Email mapRow(ResultSet resultSet, int i) throws SQLException {
        Email email = new Email();

        email.setId(resultSet.getInt("id"));
        email.setSendDate(resultSet.getObject("send_date", LocalDate.class));
        email.setSender(resultSet.getString("sender"));
        email.setReceiver(resultSet.getString("receiver"));
        email.setSubject(resultSet.getString("subject"));
        email.setBody(resultSet.getString("body"));

        return email;
    }
}
