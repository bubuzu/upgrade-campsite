package com.example.restservice.storage;

import com.example.restservice.model.Booking;
import com.example.restservice.model.FreeDates;
import com.example.restservice.util.DateHelper;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleStorage implements BookingStore, AvailabilityStore {

    private final ConcurrentHashMap<String, Booking> bookings = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Date, String> availability = new ConcurrentHashMap<>();

    public Booking getBooking(String bookingId) {
        return bookings.get(bookingId);
    }

    @Override
    public boolean addBooking(Booking booking) {
        return bookings.putIfAbsent(booking.getId(), booking) == null;
    }

    @Override
    public Booking deleteBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        bookings.remove(bookingId);
        return booking;
    }

    @Override
    public boolean reserveSpot(Date date, String bookingId) {
        return availability.putIfAbsent(date, bookingId) == null;
    }

    @Override
    public void cancelSpot(Date date, String bookingId) {
        if (availability.get(date).equals(bookingId)){
            availability.remove(date);
        }
    }

    @Override
    public FreeDates getFreeDates(Date from, Date to) {
        FreeDates result = new FreeDates();

        Date current = from;
        while (!to.before(current)){
            String isFree = availability.get(current);
            if (isFree == null){
                result.getFreeDates().add(current);
            }

            current = DateHelper.incrementDate(current, 1);
        }

        return result;
    }
}
