package com.aams.skillsharing.dao;

import com.aams.skillsharing.model.AssessmentScore;
import com.aams.skillsharing.model.Collaboration;
import com.aams.skillsharing.model.CollaborationState;
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
        collaboration.setAssessment(AssessmentScore.fromId(rs.getString("assessment")));
        collaboration.setState(CollaborationState.fromId(rs.getString("state")));

        return collaboration;
    }
}
