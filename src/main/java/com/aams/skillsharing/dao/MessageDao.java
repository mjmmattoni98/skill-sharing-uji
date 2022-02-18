package com.aams.skillsharing.dao;

import com.aams.skillsharing.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MessageDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    public void addMessage(Message message) throws DuplicateKeyException {
        jdbcTemplate.update("INSERT INTO message VALUES (?,?,?,?)",
                message.getIdOffer(),
                message.getIdRequest(),
                message.getText(),
                message.getDateTime()
        );
    }

    public void deleteMessage(Message message) {
        jdbcTemplate.update("DELETE FROM message WHERE id_offer = ? AND id_request = ? AND date_time = ?",
                message.getIdOffer(),
                message.getIdRequest(),
                message.getDateTime()
        );
    }

    public void deleteMessage(int idOffer, int idRequest, LocalDateTime dateTime){
        jdbcTemplate.update("DELETE FROM message WHERE id_offer = ? AND id_request = ? AND date_time = ?",
                idOffer,
                idRequest,
                dateTime
        );
    }

    public void updateMessage(Message message) {
        jdbcTemplate.update("UPDATE message SET id_offer = ?, id_request = ?, text = ?, date_time = ? WHERE id_offer = ? AND id_request = ? AND date_time = ?",
                message.getIdOffer(),
                message.getIdRequest(),
                message.getText(),
                message.getDateTime(),
                message.getIdOffer(),
                message.getIdRequest(),
                message.getDateTime()
        );
    }

    public Message getMessage(int idOffer, int idRequest, LocalDateTime dateTime){
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM message WHERE id_offer = ? AND id_request = ? AND date_time = ?",
                    new MessageRowMapper(),
                    idOffer,
                    idRequest,
                    dateTime
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Message> getMessages(int idOffer, int idRequest){
        try {
            return jdbcTemplate.query("SELECT * FROM message WHERE id_offer = ? AND id_request = ?",
                    new MessageRowMapper(),
                    idOffer,
                    idRequest
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}
