package com.aams.skillsharing.dao;

import com.aams.skillsharing.model.Skill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SkillDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    public void addSkill(Skill skill) throws DuplicateKeyException {
        jdbcTemplate.update("INSERT INTO skill VALUES (?,?,?::skill_level,?)",
                skill.getName(),
                skill.getDescription(),
                skill.getLevel(),
                skill.isCanceled()
        );
    }

    public void deleteSkill(Skill skill) {
        jdbcTemplate.update("DELETE FROM skill WHERE name = ?",
                skill.getName()
        );
    }

    public void deleteSkill(String name){
        jdbcTemplate.update("DELETE FROM skill WHERE name = ?",
                name
        );
    }

    public void updateSkill(Skill skill) {
        jdbcTemplate.update("UPDATE skill SET description = ?, level = ?::skill_level, canceled = ? WHERE name = ?",
                skill.getDescription(),
                skill.getLevel(),
                skill.isCanceled(),
                skill.getName()
        );
    }

    public Skill getSkill(String name){
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM skill WHERE name = ?",
                    new SkillRowMapper(),
                    name
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Skill> getSkills(){
        try {
            return jdbcTemplate.query("SELECT * FROM skill",
                    new SkillRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Skill> getSkillsByName(String name){
        try {
            return jdbcTemplate.query("SELECT * FROM skill WHERE LOWER(name) LIKE ?",
                    new SkillRowMapper(),
                    "%" + name.toLowerCase() + "%"
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Skill> getAvailableSkills () {
        try {
            return jdbcTemplate.query("select * from skill WHERE canceled = false",
                    new SkillRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Skill> getDisabledSkills () {
        try {
            return jdbcTemplate.query("select * from skill WHERE canceled = true",
                    new SkillRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Skill> getAvailableSkillsByName (String name) {
        try {
            return jdbcTemplate.query("select * from skill WHERE canceled = false AND LOWER(name) LIKE ?",
                    new SkillRowMapper(),
                    "%" + name.toLowerCase() + "%"
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Skill> getDisabledSkillsByName (String name) {
        try {
            return jdbcTemplate.query("select * from skill WHERE canceled = true AND LOWER(name) LIKE ?",
                    new SkillRowMapper(),
                    "%" + name.toLowerCase() + "%"
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Skill> getSkillsOfUsernames (String username) {
        try {
            return jdbcTemplate.query("SELECT s.* FROM skill AS s " +
                                        "JOIN request AS r ON s.name = r.name " +
                                        "JOIN offer AS o ON s.name = o.name " +
                                        " WHERE (r.username = ? OR o.username = ?)",
                    new SkillRowMapper(),
                    username,
                    username);
        }
        catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}
