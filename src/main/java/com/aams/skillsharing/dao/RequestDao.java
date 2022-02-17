package com.aams.skillsharing.dao;

import com.aams.skillsharing.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RequestDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    public void addRequest(Request request) throws DuplicateKeyException {
        jdbcTemplate.update("INSERT INTO request VALUES (?,?,?,?,?,?)",
                request.getId(),
                request.getName(),
                request.getUsername(),
                request.getStartDate(),
                request.getFinishDate(),
                request.getDescription()
        );
    }

    public void deleteRequest(Request request) {
        jdbcTemplate.update("DELETE FROM request WHERE id = ?",
                request.getId()
        );
    }

    public void deleteRequest(int id){
        jdbcTemplate.update("DELETE FROM request WHERE id = ?",
                id
        );
    }

    public void updateRequest(Request request) {
        jdbcTemplate.update("UPDATE request SET id = ?, name = ?, username = ?, start_date = ?, finish_date = ?, description = ? WHERE id = ?",
                request.getId(),
                request.getName(),
                request.getUsername(),
                request.getStartDate(),
                request.getFinishDate(),
                request.getDescription(),
                request.getId()
        );
    }

    public Request getRequest(int id){
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM request WHERE id = ?",
                    new RequestRowMapper(),
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Request> getRequests(){
        try {
            return jdbcTemplate.query("SELECT * FROM request",
                    new RequestRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}
