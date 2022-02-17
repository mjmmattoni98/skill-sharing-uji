package com.aams.skillsharing.dao;

import com.aams.skillsharing.model.InternalUser;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<InternalUser> {
    public InternalUser mapRow(ResultSet rs, int i) throws SQLException {
        InternalUser internalUser = new InternalUser();
        internalUser.setUsername(rs.getString("username"));
        internalUser.setPassword(rs.getString("password"));
        internalUser.setSkp(rs.getBoolean("is_skp"));

        return internalUser;
    }
}
