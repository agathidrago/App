package com.example.app.exception;

import lombok.Getter;

/**
 * Represents the structure of an error response containing error details and a message.
 */

@Getter
public class ErrorResponse {
    private String error;
    private String message;

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }

}