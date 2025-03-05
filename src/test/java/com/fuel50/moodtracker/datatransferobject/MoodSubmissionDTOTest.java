package com.fuel50.moodtracker.datatransferobject;

import com.fuel50.moodtracker.domainvalue.MoodType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MoodSubmissionDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidMoodSubmission() {
        // Arrange
        MoodSubmissionDTO dto = new MoodSubmissionDTO(MoodType.HAPPY, UUID.randomUUID().toString(), "This is a valid comment");
        
        // Act
        Set<ConstraintViolation<MoodSubmissionDTO>> violations = validator.validate(dto);
        
        // Assert
        assertTrue(violations.isEmpty(), "Valid mood submission should not have validation errors");
    }

    @Test
    void testMoodSubmission_NullMood() {
        // Arrange
        MoodSubmissionDTO dto = new MoodSubmissionDTO(null, UUID.randomUUID().toString(), "This is a comment");
        
        // Act
        Set<ConstraintViolation<MoodSubmissionDTO>> violations = validator.validate(dto);
        
        // Assert
        assertEquals(1, violations.size(), "Should have 1 validation error");
        assertEquals("Mood can not be null!", violations.iterator().next().getMessage());
    }

    @Test
    void testMoodSubmission_LongComment() {
        // Arrange
        StringBuilder commentBuilder = new StringBuilder();
        // Create a comment with 351 characters (exceeding the 350 limit)
        for (int i = 0; i < 351; i++) {
            commentBuilder.append("a");
        }
        
        MoodSubmissionDTO dto = new MoodSubmissionDTO(MoodType.A_BIT_MEH, UUID.randomUUID().toString(), commentBuilder.toString());
        
        // Act
        Set<ConstraintViolation<MoodSubmissionDTO>> violations = validator.validate(dto);
        
        // Assert
        assertEquals(1, violations.size(), "Should have 1 validation error");
        assertEquals("Comment can not be longer than 350 characters!", violations.iterator().next().getMessage());
    }

    @Test
    void testBuilder() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        String comment = "Test comment";
        
        // Act
        MoodSubmissionDTO dto = MoodSubmissionDTO.newBuilder()
                .setMood(MoodType.GRUMPY)
                .setUserId(userId)
                .setComment(comment)
                .createMoodSubmissionDTO();
        
        // Assert
        assertEquals(MoodType.GRUMPY, dto.getMood());
        assertEquals(userId, dto.getUserId());
        assertEquals(comment, dto.getComment());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        MoodSubmissionDTO dto = new MoodSubmissionDTO(MoodType.HAPPY, "original-user", "Original comment");
        
        // Act
        dto.setMood(MoodType.STRESSED_OUT_NOT_A_HAPPY_CAMPER);
        dto.setUserId("updated-user");
        dto.setComment("Updated comment");
        
        // Assert
        assertEquals(MoodType.STRESSED_OUT_NOT_A_HAPPY_CAMPER, dto.getMood());
        assertEquals("updated-user", dto.getUserId());
        assertEquals("Updated comment", dto.getComment());
    }
}