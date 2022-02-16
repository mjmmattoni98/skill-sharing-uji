package com.aams.skillsharing.model;

import lombok.Data;

@Data
public class Student {
    private String name;
    private String surname;
    private String email;
    private String password;
    private String username;
    private int balanceHours = 0;
    private boolean isBlocked = false;
    private String street;
    private int number;
    private int pc;
    private String locality;
    private boolean isSkp = false;
    private String degree;
}
