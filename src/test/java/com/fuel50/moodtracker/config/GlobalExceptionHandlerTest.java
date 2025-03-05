package com.fuel50.moodtracker.config;

import com.fuel50.moodtracker.exception.DuplicateMoodSubmissionException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleAlreadySubmittedException() {
        // Arrange
        DuplicateMoodSubmissionException exception = new DuplicateMoodSubmissionException("Already submitted today");
        
        // Act
        ResponseEntity<String> response = exceptionHandler.handleAlreadySubmittedException(exception);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Already submitted today", response.getBody());
    }

    @Test
    void testHandleValidationExceptions() {
        // Arrange
        MethodArgumentNotValidException mockException = createMethodArgumentNotValidException("Mood can not be null!");
        
        // Act
        ResponseEntity<String> response = exceptionHandler.handleValidationExceptions(mockException);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Mood can not be null!", response.getBody());
    }

    @Test
    void testHandleValidationExceptions_NoErrorMessage() {
        // Arrange
        MethodArgumentNotValidException mockException = createMethodArgumentNotValidException(null);
        
        // Act
        ResponseEntity<String> response = exceptionHandler.handleValidationExceptions(mockException);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation error", response.getBody());
    }

    private MethodArgumentNotValidException createMethodArgumentNotValidException(String errorMessage) {
        BindingResult bindingResult = mock(BindingResult.class);
        MethodParameter methodParameter = mock(MethodParameter.class);
        
        if (errorMessage != null) {
            FieldError fieldError = new FieldError("objectName", "fieldName", errorMessage);
            when(bindingResult.getAllErrors()).thenReturn(java.util.Collections.singletonList(fieldError));
        } else {
            when(bindingResult.getAllErrors()).thenReturn(java.util.Collections.emptyList());
        }
        
        return new MethodArgumentNotValidException(methodParameter, bindingResult);
    }
}