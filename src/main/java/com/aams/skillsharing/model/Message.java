package com.aams.skillsharing.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class Message implements Comparable<Message> {
    private int idOffer;
    private int idRequest;
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateTime;
    private String text;

    @Override
    public int compareTo(Message o) {
        return this.dateTime.compareTo(o.dateTime);
    }
}