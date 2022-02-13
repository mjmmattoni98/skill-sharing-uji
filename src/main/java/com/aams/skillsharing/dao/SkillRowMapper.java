package com.aams.skillsharing.dao;

import com.aams.skillsharing.model.Skill;
import com.aams.skillsharing.model.SkillLevel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class SkillRowMapper implements RowMapper<Skill> {
    @Override
    public Skill mapRow(ResultSet rs, int rowNum) throws SQLException {
        Skill skill = new Skill();
        skill.setName(rs.getString("name"));
        skill.setDescription(rs.getString("description"));
        skill.setLevel(SkillLevel.fromId(rs.getString("level")));
        skill.setStartDate(rs.getObject("start_date", LocalDate.class));
        skill.setFinishDate(rs.getObject("finish_date", LocalDate.class));

        return skill;
    }
}
