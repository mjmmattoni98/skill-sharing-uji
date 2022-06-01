package com.aams.skillsharing.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class Email implements Comparable<Email> {
    private int id;
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    private LocalDate sendDate;
    private String sender;
    private String receiver;
    private String subject;
    private String body;

    @Override
    public int compareTo(Email o) {
        return this.sendDate.compareTo(o.sendDate);
    }
}
