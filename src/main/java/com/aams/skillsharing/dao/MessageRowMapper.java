package com.aams.skillsharing.dao;

import com.aams.skillsharing.model.Message;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class MessageRowMapper implements RowMapper<Message> {

    @Override
    public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
        Message message = new Message();
        message.setIdOffer(rs.getInt("id_offer"));
        message.setIdRequest(rs.getInt("id_request"));
        message.setText(rs.getString("text"));
        message.setDateTime(rs.getObject("date_time", LocalDateTime.class));

        return message;
    }
}
