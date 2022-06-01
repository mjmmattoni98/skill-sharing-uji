package com.aams.skillsharing.dao;

import com.aams.skillsharing.model.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OfferDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    public void addOffer(Offer offer) throws DuplicateKeyException {
        jdbcTemplate.update("INSERT INTO offer(name, username, start_date, finish_date, description, canceled) VALUES (?,?,?,?,?,?)",
                offer.getName(),
                offer.getUsername(),
                offer.getStartDate(),
                offer.getFinishDate(),
                offer.getDescription(),
                offer.isCanceled()
        );
    }

    public void deleteOffer(Offer offer) {
        jdbcTemplate.update("DELETE FROM offer WHERE id = ?",
                offer.getId()
        );
    }

    public void deleteOffer(int id) {
        jdbcTemplate.update("DELETE FROM offer WHERE id = ?",
                id
        );
    }

    public void updateOffer(Offer offer) {
        jdbcTemplate.update("UPDATE offer SET name = ?, username = ?, start_date = ?, finish_date = ?, " +
                        "description = ?, canceled = ? WHERE id = ?",
                offer.getName(),
                offer.getUsername(),
                offer.getStartDate(),
                offer.getFinishDate(),
                offer.getDescription(),
                offer.isCanceled(),
                offer.getId()
        );
    }

    public Offer getOffer(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM offer WHERE id = ?",
                    new OfferRowMapper(),
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Offer> getOffers() {
        try {
            return jdbcTemplate.query("SELECT * FROM offer WHERE canceled = false AND (finish_date IS NULL OR finish_date >= CURRENT_DATE)",
                    new OfferRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Offer> getOffersByUsername(String username) {
        try {
            return jdbcTemplate.query("SELECT * FROM offer WHERE canceled = false AND LOWER(username) LIKE ? " +
                            "AND (finish_date IS NULL OR finish_date >= CURRENT_DATE)",
                    new OfferRowMapper(),
                    "%" + username.toLowerCase() + "%"
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Offer> getOffersStudent(String username) {
        try {
            return jdbcTemplate.query("SELECT * FROM offer WHERE username = ? AND canceled = false AND " +
                            "(finish_date IS NULL OR finish_date >= CURRENT_DATE)",
                    new OfferRowMapper(),
                    username
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Offer> getOffersStudentBySkill(String username, String skill) {
        try {
            return jdbcTemplate.query("SELECT * FROM offer WHERE LOWER(name) LIKE ? AND username = ? AND canceled = false AND " +
                            "(finish_date IS NULL OR finish_date >= CURRENT_DATE)",
                    new OfferRowMapper(),
                    "%" + skill.toLowerCase() + "%",
                    username
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Offer> getOffersStudentSkill(String username, String skill) {
        try {
            return jdbcTemplate.query("SELECT * FROM offer WHERE username = ? AND name = ? AND canceled = false AND " +
                            "(finish_date IS NULL OR finish_date >= CURRENT_DATE)",
                    new OfferRowMapper(),
                    username,
                    skill
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Offer> getOffersSkill(String name) {
        try {
            return jdbcTemplate.query("SELECT * FROM offer WHERE name = ? AND canceled = false AND " +
                            "(finish_date IS NULL OR finish_date >= CURRENT_DATE)",
                    new OfferRowMapper(),
                    name
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Offer> getOffersSkillByUsername(String name, String username) {
        try {
            return jdbcTemplate.query("SELECT * FROM offer WHERE name = ? AND canceled = false AND " +
                            "(finish_date IS NULL OR finish_date >= CURRENT_DATE) AND LOWER(username) LIKE ?",
                    new OfferRowMapper(),
                    name,
                    "%" + username.toLowerCase() + "%"
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Offer> getOffersSkillNotCollaborating(String name) {
        try {
            return jdbcTemplate.query("SELECT * FROM offer WHERE name = ? AND canceled = false AND " +
                            "id NOT IN (SELECT id_offer FROM collaboration) AND (finish_date IS NULL OR finish_date >= CURRENT_DATE)",
                    new OfferRowMapper(),
                    name
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Offer> fetchLastThreeOffers(String name) {
        try {
            return jdbcTemplate.query("SELECT * FROM offer WHERE username = ? AND canceled = false " +
                                            "AND (finish_date IS NULL OR finish_date >= CURRENT_DATE) " +
                                            "ORDER BY id DESC LIMIT 3",
                new OfferRowMapper(),
                name
            );
        }
        catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

}
