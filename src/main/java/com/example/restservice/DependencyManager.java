package com.example.restservice;

import com.example.restservice.storage.AvailabilityStore;
import com.example.restservice.storage.BookingStore;
import com.example.restservice.storage.SimpleStorage;

public class DependencyManager {
    private static SimpleStorage single_instance = null;

    private DependencyManager() {
    }

    public static BookingStore getBookingStore() {
        return getSimpleStorage();
    }

    public static AvailabilityStore getAvailabilityStore() {
        return getSimpleStorage();
    }

    private static SimpleStorage getSimpleStorage() {
        if (single_instance == null)
            single_instance = new SimpleStorage();

        return single_instance;
    }

}
