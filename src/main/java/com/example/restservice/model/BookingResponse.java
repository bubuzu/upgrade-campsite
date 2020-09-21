package com.example.restservice.model;

public class BookingResponse extends BasicResponse {
    private final Booking booking;

    public BookingResponse(boolean success, String message, Booking booking) {
        super(success, message);
        this.booking = booking;
    }

    public Booking getBooking() {
        return booking;
    }
}
