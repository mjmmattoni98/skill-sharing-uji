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
            List<InternalUser> internalUsers = jdbcTemplate.query("SELECT username, password, is_skp, balance_hours, email " +
                            " FROM student;",
                    new UserRowMapper()
            );
            for(InternalUser user : internalUsers){
//                user.setPassword(encryptor.encryptPassword(user.getPassword()));
                knownUsers.put(user.getUsername(), user);
            }
        } catch (EmptyResultDataAccessException ignored){}

        return  knownUsers;
    }

    @Override
    public InternalUser loadUserByUsername(String username) {//}, String password) {
        /*InternalUser user = getUserList().get(username.trim());
        if (user == null) {
            return null;
        }
        return user;*/
/*
//        password viene del model, en claro
        System.out.println(password);
        System.out.println(user.getPassword());

        if (encryptor.checkPassword(password, user.getPassword())) {
            return user;
        } else {
            return null;
        }*/

        return getUserList().get(username.trim());
    }

    @Override
    public Collection<InternalUser> listAllUsers() {
        return getUserList().values();
    }
}
