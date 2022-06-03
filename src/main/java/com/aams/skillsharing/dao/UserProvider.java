package com.aams.skillsharing.dao;

import com.aams.skillsharing.model.InternalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserProvider implements UserDao{
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Map<String, InternalUser> getUserList(){
        Map<String, InternalUser> knownUsers = new HashMap<>();

        try {
            List<InternalUser> internalUsers = jdbcTemplate.query("SELECT username, password, is_skp, balance_hours, email " +
                            " FROM student;",
                    new UserRowMapper()
            );
            for(InternalUser user : internalUsers){
                knownUsers.put(user.getUsername(), user);
            }
        } catch (EmptyResultDataAccessException ignored){}

        return  knownUsers;
    }

    @Override
    public InternalUser loadUserByUsername(String username) {
        return getUserList().get(username.trim());
    }

    @Override
    public Collection<InternalUser> listAllUsers() {
        return getUserList().values();
    }
}
