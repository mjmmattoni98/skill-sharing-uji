package com.aams.skillsharing.dao;

import com.aams.skillsharing.model.InternalUser;

import java.util.Collection;

public interface UserDao {
    InternalUser loadUserByUsername(String user);//, String password);
    Collection<InternalUser> listAllUsers();
}
