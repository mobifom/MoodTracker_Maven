package com.fuel50.moodtracker.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DuplicateMoodSubmissionExceptionTest {

    @Test
    void testDuplicateMoodSubmissionException() {
        // Arrange
        String errorMessage = "User has already submitted mood today";
        
        // Act
        DuplicateMoodSubmissionException exception = new DuplicateMoodSubmissionException(errorMessage);
        
        // Assert
        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testExceptionInheritance() {
        // Arrange & Act
        DuplicateMoodSubmissionException exception = new DuplicateMoodSubmissionException("Test message");
        
        // Assert
        assertTrue(exception instanceof RuntimeException, 
                "DuplicateMoodSubmissionException should be a subclass of RuntimeException");
    }
}