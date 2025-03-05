package com.fuel50.moodtracker.domainobject;

import com.fuel50.moodtracker.domainvalue.MoodType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class MoodSubmissionDOTest {

    @Test
    void testMoodSubmissionDO_DefaultConstructor() {
        // Arrange & Act
        MoodSubmissionDO moodSubmission = new MoodSubmissionDO();
        
        // Assert
        assertNull(moodSubmission.getId());
        assertNull(moodSubmission.getMood());
        assertNull(moodSubmission.getUserId());
        assertNull(moodSubmission.getComment());
        assertNotNull(moodSubmission.getSubmissionDate(), "Submission date should be initialized");
        LocalDateTime now = LocalDateTime.now();
        assertTrue(ChronoUnit.SECONDS.between(moodSubmission.getSubmissionDate(), now) < 5, 
                "Submission date should be close to current time");
    }

    @Test
    void testMoodSubmissionDO_ParameterizedConstructor() {
        // Arrange & Act
        MoodSubmissionDO moodSubmission = new MoodSubmissionDO(MoodType.HAPPY, "test-user", "Test comment");
        
        // Assert
        assertNull(moodSubmission.getId());
        assertEquals(MoodType.HAPPY, moodSubmission.getMood());
        assertEquals("test-user", moodSubmission.getUserId());
        assertEquals("Test comment", moodSubmission.getComment());
        assertNotNull(moodSubmission.getSubmissionDate());
    }

    @Test
    void testMoodSubmissionDO_SettersAndGetters() {
        // Arrange
        MoodSubmissionDO moodSubmission = new MoodSubmissionDO();
        Long id = 1L;
        MoodType mood = MoodType.GRUMPY;
        String userId = "user-123";
        String comment = "Updated comment";
        LocalDateTime submissionDate = LocalDateTime.now().minusHours(1);
        
        // Act
        moodSubmission.setId(id);
        moodSubmission.setMood(mood);
        moodSubmission.setUserId(userId);
        moodSubmission.setComment(comment);
        moodSubmission.setSubmissionDate(submissionDate);
        
        // Assert
        assertEquals(id, moodSubmission.getId());
        assertEquals(mood, moodSubmission.getMood());
        assertEquals(userId, moodSubmission.getUserId());
        assertEquals(comment, moodSubmission.getComment());
        assertEquals(submissionDate, moodSubmission.getSubmissionDate());
    }

    @Test
    void testMoodSubmissionDO_NullValues() {
        // Arrange
        MoodSubmissionDO moodSubmission = new MoodSubmissionDO(MoodType.A_BIT_MEH, "test-user", "Initial comment");
        
        // Act
        moodSubmission.setComment(null);
        
        // Assert
        assertNull(moodSubmission.getComment(), "Comment should be set to null");
        assertEquals(MoodType.A_BIT_MEH, moodSubmission.getMood(), "Mood should remain unchanged");
        assertEquals("test-user", moodSubmission.getUserId(), "User ID should remain unchanged");
    }
}