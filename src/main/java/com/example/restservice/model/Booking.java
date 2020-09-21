package com.example.restservice.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Booking {
    private final String id;
    private final List<Date> dates;
    private final String name;

    public Booking(String customerName) {
        this.id = UUID.randomUUID().toString();
        this.dates = new ArrayList<>(3);
        this.name = customerName;
    }

    public String getId() {
        return id;
    }

    public List<Date> getDates() {
        return dates;
    }

    public String getName() {
        return name;
    }
}
