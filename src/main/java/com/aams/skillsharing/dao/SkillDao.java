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
        jdbcTemplate.update("INSERT INTO skill VALUES (?,?,?,?,?)",
                skill.getName(),
                skill.getDescription(),
                skill.getLevel(),
                skill.getStartDate(),
                skill.getFinishDate()
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
        jdbcTemplate.update("UPDATE skill SET name = ?, description = ?, level = ?, start_date = ?, finish_date = ? WHERE name = ?",
                skill.getName(),
                skill.getDescription(),
                skill.getLevel(),
                skill.getStartDate(),
                skill.getFinishDate(),
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
}
