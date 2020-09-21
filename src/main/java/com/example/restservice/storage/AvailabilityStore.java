package com.example.restservice.storage;

import com.example.restservice.model.FreeDates;

import java.util.Date;

public interface AvailabilityStore {
    FreeDates getFreeDates(final Date from, final Date to);
    boolean reserveSpot(Date date, String bookingId);
    void cancelSpot(Date date, String bookingId);
}
