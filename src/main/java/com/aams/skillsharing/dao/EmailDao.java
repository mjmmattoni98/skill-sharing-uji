package com.aams.skillsharing.dao;

import com.aams.skillsharing.model.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EmailDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    public void addEmail(Email email) {
        jdbcTemplate.update("INSERT INTO email(send_date, sender, receiver, subject, body) VALUES(?,?,?,?,?)",
                email.getSendDate(),
                email.getSender(),
                email.getReceiver(),
                email.getSubject(),
                email.getBody()
        );
    }

    public void deleteEmail(Email email) {
        jdbcTemplate.update("DELETE FROM email WHERE id=?",
                email.getId());
    }

    public void deleteEmail(int id) {
        jdbcTemplate.update("DELETE FROM email WHERE id=?", id);
    }

    public Email getEmail(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM email WHERE id=?",
                    new EmailRowMapper(),
                    id
            );
        }
        catch (EmptyResultDataAccessException e) {
            return null ;
        }
    }

    public List<Email> getEmails(String receiver) {
        try {
            return jdbcTemplate.query("SELECT * FROM email WHERE receiver=?",
                    new EmailRowMapper(),
                    receiver
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}
