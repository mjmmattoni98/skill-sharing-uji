package com.aams.skillsharing.dao;

import com.aams.skillsharing.model.InternalUser;
import org.jasypt.util.password.BasicPasswordEncryptor;
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
    private final BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Map<String, InternalUser> getUserList(){
        Map<String, InternalUser> knownUsers = new HashMap<>();

        try {
            List<InternalUser> internalUsers = jdbcTemplate.query("SELECT user, password, is_skp FROM student;",
                    new UserRowMapper()
            );
            for(InternalUser user : internalUsers){
                user.setPassword(encryptor.encryptPassword(user.getPassword()));
                knownUsers.put(user.getUser(), user);
            }
        } catch (EmptyResultDataAccessException ignored){}

        return  knownUsers;
    }

    @Override
    public InternalUser loadUserByUsername(String username, String password) {
        InternalUser user = getUserList().get(username.trim());
        if (user == null) {
            return null;
        }

        if (encryptor.checkPassword(password, user.getPassword())) {
            return user;
        } else {
            return null;
        }
    }

    @Override
    public Collection<InternalUser> listAllUsers() {
        return getUserList().values();
    }
}
