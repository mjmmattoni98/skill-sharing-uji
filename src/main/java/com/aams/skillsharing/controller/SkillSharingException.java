package com.aams.skillsharing.controller;

import lombok.Data;

@Data
public class SkillSharingException extends RuntimeException{
    private String message;
    private String errorName;
    private String path;

    public SkillSharingException(String message, String errorName, String path) {
        this.message = message;
        this.errorName = errorName;
        this.path = path;
    }
}
