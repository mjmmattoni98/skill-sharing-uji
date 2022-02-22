package com.aams.skillsharing.dao;

import com.aams.skillsharing.model.Collaboration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CollaborationDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    public void addCollaboration(Collaboration collaboration) throws DuplicateKeyException {
        jdbcTemplate.update("INSERT INTO collaboration VALUES (?,?,?,?,?)",
                collaboration.getIdOffer(),
                collaboration.getIdRequest(),
                collaboration.getHours(),
                collaboration.getAssessment().getId(),
                collaboration.getState().getId()
        );
    }

    public void deleteCollaboration(Collaboration collaboration) {
        jdbcTemplate.update("DELETE FROM collaboration WHERE id_offer = ? AND id_request = ?",
                collaboration.getIdOffer(),
                collaboration.getIdRequest()
        );
    }

    public void deleteCollaboration(int idOffer, int idRequest) {
        jdbcTemplate.update("DELETE FROM collaboration WHERE id_offer = ? AND id_request = ?",
                idOffer,
                idRequest
        );
    }

    public void updateCollaboration(Collaboration collaboration) {
        jdbcTemplate.update("UPDATE collaboration SET id_offer = ?, id_request = ?, hours = ?, assessment = ?, state = ? WHERE id_offer = ? AND id_request = ?",
                collaboration.getIdOffer(),
                collaboration.getIdRequest(),
                collaboration.getHours(),
                collaboration.getAssessment().getId(),
                collaboration.getState().getId(),
                collaboration.getIdOffer(),
                collaboration.getIdRequest()
        );
    }

    public Collaboration getCollaboration(int idOffer, int idRequest) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM collaboration WHERE id_offer = ? AND id_request = ?",
                    new CollaborationRowMapper(),
                    idOffer,
                    idRequest
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Collaboration> getCollaborations(){
        try {
            return jdbcTemplate.query("SELECT id_offer, id_request, hours, assessment, state, o.username as student_offer, " +
                            "r.username as student_request FROM collaboration JOIN offer o ON collaboration.id_offer = o.id " +
                            "JOIN request r ON collaboration.id_request = r.id",
                    new CollaborationRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Collaboration> getCollaborationsStudent(String username){
        try {
            return jdbcTemplate.query("SELECT id_offer, id_request, hours, assessment, state, o.username as student_offer, " +
                            "r.username as student_request FROM collaboration JOIN offer o ON collaboration.id_offer = o.id " +
                            "JOIN request r ON collaboration.id_request = r.id WHERE " +
                            "id_request IN (SELECT id FROM request WHERE username = ?) " +
                            "OR id_offer IN (SELECT id FROM offer WHERE username = ?)",
                    new CollaborationRowMapper(),
                    username,
                    username
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}
