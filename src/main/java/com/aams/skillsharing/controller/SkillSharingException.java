package com.aams.skillsharing.controller;

import lombok.Getter;
import lombok.Setter;

public class SkillSharingException extends RuntimeException{
    @Getter @Setter
    private String message;
    @Getter @Setter
    private String errorName;
    @Getter @Setter
    private String path;

    public SkillSharingException(String message, String errorName, String path) {
        this.message = message;
        this.errorName = errorName;
        this.path = path;
    }
}
