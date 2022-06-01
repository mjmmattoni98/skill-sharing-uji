package com.aams.skillsharing.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Locale;

@Data
public class Offer implements Comparable<Offer> {
    private int id;
    private String username;
    private String name;
    private String description;
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    private LocalDate finishDate = null;
    private boolean canceled = false;
    private boolean fromSkill;

    @Override
    public int compareTo(Offer o) {
        return this.getName().toLowerCase().compareTo(o.getName().toLowerCase());
    }
}
