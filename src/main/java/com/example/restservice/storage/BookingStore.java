package com.example.restservice.storage;

import com.example.restservice.model.Booking;

public interface BookingStore {
    Booking getBooking(final String bookingId);
    boolean addBooking(Booking booking);
    Booking deleteBooking(final String bookingId);
}
