package ru.soshnin.spring.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Visit {
    private int id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfVisit;

    public Visit() {
    }

    public Visit(int id, Date dateOfVisit) {
        this.id = id;
        this.dateOfVisit = dateOfVisit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateOfVisit() {
        return dateOfVisit;
    }

    public void setDateOfVisit(Date dateOfVisit) {
        this.dateOfVisit = dateOfVisit;
    }

    @Override
    public String toString() {
        return "Visit{" +
                "id=" + id +
                ", dateOfVisit=" + dateOfVisit +
                '}';
    }
}
