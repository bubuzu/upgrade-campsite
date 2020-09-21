package com.example.restservice;

import com.example.restservice.exception.InvalidParameterException;
import com.example.restservice.exception.InvalidTimeIntervalException;
import com.example.restservice.model.BasicResponse;
import com.example.restservice.model.Booking;
import com.example.restservice.model.BookingResponse;
import com.example.restservice.model.FreeDates;
import com.example.restservice.storage.AvailabilityStore;
import com.example.restservice.storage.BookingStore;
import com.example.restservice.util.DateHelper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class CampsiteController {

    private final AvailabilityStore availabilityStore = DependencyManager.getAvailabilityStore();
    private final BookingStore bookingStore = DependencyManager.getBookingStore();

    /**
     * Endpoint to get campsite free dates
     * @param from date from (inclusive), format: "yyyy-MM-dd"
     * @param to date to (inclusive), format: "yyyy-MM-dd"
     * @return list of free dates
     * @throws InvalidTimeIntervalException for incorrect input
     */
    @GetMapping("/free-dates")
    public FreeDates freeDates(
        @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
        @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) throws InvalidTimeIntervalException {

        to = invalidateFreeInput(from, to);
        return availabilityStore.getFreeDates(from, to);
    }

    /**
     * Endpoint to make a reservation
     * @param from date from (inclusive), format: "yyyy-MM-dd"
     * @param to date to (inclusive), format: "yyyy-MM-dd"
     * @param name customer name
     * @return BookingResponse with booking details
     * @throws InvalidTimeIntervalException for incorrect dates
     * @throws InvalidParameterException for incorrect name
     */
    @PostMapping("/book")
    public BookingResponse book(
        @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
        @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
        String name) throws InvalidTimeIntervalException, InvalidParameterException {

        invalidateBookInput(from, to, name);

        Date date = from;
        boolean bookedSuccessfully = true;

        Booking booking = new Booking(name);

        while (!to.before(date) && bookedSuccessfully) {
            bookedSuccessfully = availabilityStore.reserveSpot(date, booking.getId());
            if (bookedSuccessfully) {
                booking.getDates().add(date);
                date = DateHelper.incrementDate(date, 1);
            }
        }

        if (!bookedSuccessfully) {
            cancelSpots(booking);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return new BookingResponse(
                false,
                String.format("Failed to make a reservation. Date: %s is already booked", formatter.format(date)),
            null);
        }

        bookingStore.addBooking(booking);
        return new BookingResponse(true, "Have a great vacations", booking);
    }

    /**
     * Endpoint to cancel booking
     * @param bookingId uniq booking ID
     * @return cancellation result
     * @throws InvalidParameterException then bookingId is missing
     */
    @PostMapping("/booking/cancel")
    public BasicResponse cancelBooking(String bookingId) throws InvalidParameterException {
        if (bookingId == null){
            throw  new InvalidParameterException("bookingId parameter is missing");
        }

        Booking booking = bookingStore.deleteBooking(bookingId);
        if (booking == null) {
            return new BasicResponse(false, "Can't find booking with ID: " + bookingId);
        }
        cancelSpots(booking);
        return new BasicResponse(true, "Successfully canceled. Booking ID: " + bookingId);
    }

    /**
     * Endpoint to view booking
     * @param bookingId uniq booking ID
     * @return booking confirmation
     * @throws InvalidParameterException then bookingId is missing
     */
    @GetMapping("/booking")
    public Booking viewBooking(String bookingId) throws InvalidParameterException {
        if (bookingId == null){
            throw  new InvalidParameterException("bookingId parameter is missing");
        }
        return bookingStore.getBooking(bookingId);
    }

    private void cancelSpots(Booking booking) {
        for (Date d : booking.getDates()) {
            availabilityStore.cancelSpot(d, booking.getId());
        }
    }

    private Date invalidateFreeInput(Date from, Date to) throws InvalidTimeIntervalException {
        if (from == null){
            throw new InvalidTimeIntervalException("Date 'from' can't be null");
        }

        if (to == null){
            // Show 31 days by default
            to = DateHelper.incrementDate(from, 30);
        }

        if (to.before(from)){
            throw new InvalidTimeIntervalException("Date 'to' can't be before 'from'");
        }
        return to;
    }

    private void invalidateBookInput(Date from, Date to, String name) throws InvalidParameterException, InvalidTimeIntervalException {
        if (name == null){
            throw new InvalidParameterException("Parameter 'name' can't be null");
        }

        if (from == null){
            throw new InvalidTimeIntervalException("Date 'from' can't be null");
        }

        if (to == null){
            throw new InvalidTimeIntervalException("Date 'to' can't be null");
        }

        if (DateHelper.getDifferenceInDays(from, new Date())<1){
            throw new InvalidTimeIntervalException("You can book a campsite minimum 1 day(s) ahead of arrival");
        }

        if (DateHelper.getDifferenceInDays(from, new Date())>30){
            throw new InvalidTimeIntervalException("You can book a campsite maximum 30 day(s) ahead of arrival");
        }

        if (to.before(from)){
            throw new InvalidTimeIntervalException("Date 'to' can't be before 'from'");
        }

        if (DateHelper.getDifferenceInDays(to, from)>2){
            throw new InvalidTimeIntervalException("Maximum booking period is 3 days");
        }
    }
}
