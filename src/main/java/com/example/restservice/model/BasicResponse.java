package com.example.restservice.model;

public class BasicResponse {
    private final boolean success;
    private final String message;

    public BasicResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
