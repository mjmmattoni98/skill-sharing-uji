package com.aams.skillsharing.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class InternalUser implements Serializable {
    private String user;
    private String password;
    private boolean role;

    public String getUrlMainPage() {
        if (role)
            return "espacioPublico/list";
        return "student/perfil";
    }
}
