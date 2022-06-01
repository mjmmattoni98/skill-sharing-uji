package com.aams.skillsharing.dao;

import com.aams.skillsharing.model.Collaboration;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CollaborationRowMapper  implements RowMapper<Collaboration> {

    @Override
    public Collaboration mapRow(ResultSet rs, int rowNum) throws SQLException {
        Collaboration collaboration = new Collaboration();
        collaboration.setIdOffer(rs.getInt("id_offer"));
        collaboration.setIdRequest(rs.getInt("id_request"));
        collaboration.setHours(rs.getInt("hours"));
        collaboration.setStudentOffer(rs.getString("student_offer"));
        collaboration.setStudentRequest(rs.getString("student_request"));
        collaboration.setAssessment(rs.getInt("assessment"));
        collaboration.setState(rs.getString("state"));
        collaboration.setSkill(rs.getString("skill"));

        return collaboration;
    }
}
