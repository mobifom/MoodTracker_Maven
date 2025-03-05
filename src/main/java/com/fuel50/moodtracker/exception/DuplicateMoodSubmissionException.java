package com.fuel50.moodtracker.exception;

public class DuplicateMoodSubmissionException extends RuntimeException {
    public DuplicateMoodSubmissionException(String message) {
        super(message);
    }
}
