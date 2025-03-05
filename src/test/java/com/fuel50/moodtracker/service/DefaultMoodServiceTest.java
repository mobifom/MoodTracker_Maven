package com.fuel50.moodtracker.service;

import com.fuel50.moodtracker.dataaccessobject.MoodRepository;
import com.fuel50.moodtracker.datatransferobject.TeamMoodDTO;
import com.fuel50.moodtracker.domainobject.MoodSubmissionDO;
import com.fuel50.moodtracker.domainvalue.MoodType;
import com.fuel50.moodtracker.exception.DuplicateMoodSubmissionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultMoodServiceTest {

    @Mock
    private MoodRepository moodRepository;

    @InjectMocks
    private DefaultMoodService moodService;

    private String userId;
    private MoodSubmissionDO moodSubmission;
    private LocalDateTime startOfDay;
    private LocalDateTime endOfDay;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        moodSubmission = new MoodSubmissionDO(MoodType.HAPPY, userId, "Test comment");
        
        LocalDate today = LocalDate.now();
        startOfDay = today.atStartOfDay();
        endOfDay = today.atTime(23, 59, 59);
    }

    @Test
    void testSubmitMood_Success() {
        // Arrange
        when(moodRepository.existsByUserIdAndSubmissionDateBetween(userId, startOfDay, endOfDay))
                .thenReturn(false);
        
        // Act
        moodService.submitMood(moodSubmission);
        
        // Assert
        verify(moodRepository).save(moodSubmission);
        assertNotNull(moodSubmission.getSubmissionDate(), "Submission date should be set");
    }

    @Test
    void testSubmitMood_DuplicateSubmission() {
        // Arrange
        when(moodRepository.existsByUserIdAndSubmissionDateBetween(userId, startOfDay, endOfDay))
                .thenReturn(true);
        
        // Act & Assert
        DuplicateMoodSubmissionException exception = assertThrows(
                DuplicateMoodSubmissionException.class,
                () -> moodService.submitMood(moodSubmission)
        );
        
        assertEquals("Sorry, you have already submitted your response for today, try again tomorrow!", 
                exception.getMessage());
        verify(moodRepository, never()).save(any(MoodSubmissionDO.class));
    }

    @Test
    void testGetOverallMood_NoSubmissions() {
        // Arrange
        when(moodRepository.findBySubmissionDateBetween(startOfDay, endOfDay))
                .thenReturn(Collections.emptyList());
        
        // Act
        TeamMoodDTO result = moodService.getOverallMood();
        
        // Assert
        assertNull(result.getOverallMood());
        assertTrue(result.getComments().isEmpty());
    }

    @Test
    void testGetOverallMood_MultipleSubmissions() {
        // Arrange
        List<MoodSubmissionDO> submissions = Arrays.asList(
                new MoodSubmissionDO(MoodType.HAPPY, "user1", "Great day!"),
                new MoodSubmissionDO(MoodType.JUST_NORMAL_REALLY, "user2", "Okay day"),
                new MoodSubmissionDO(MoodType.A_BIT_MEH, "user3", null)
        );
        
        when(moodRepository.findBySubmissionDateBetween(startOfDay, endOfDay))
                .thenReturn(submissions);
        
        // Act
        TeamMoodDTO result = moodService.getOverallMood();
        
        // Assert
        assertEquals(MoodType.JUST_NORMAL_REALLY, result.getOverallMood(), 
                "Average mood should be JUST_NORMAL_REALLY");
        assertEquals(2, result.getComments().size(), 
                "Should have 2 comments (null comment filtered out)");
        assertTrue(result.getComments().contains("Great day!"));
        assertTrue(result.getComments().contains("Okay day"));
    }

    @Test
    void testGetOverallMood_CalculatesCorrectOverallMood() {
        // Arrange
        List<MoodSubmissionDO> submissions = Arrays.asList(
                new MoodSubmissionDO(MoodType.HAPPY, "user1", "Comment 1"), // 5
                new MoodSubmissionDO(MoodType.HAPPY, "user2", "Comment 2"), // 5
                new MoodSubmissionDO(MoodType.GRUMPY, "user3", "Comment 3"), // 2
                new MoodSubmissionDO(MoodType.GRUMPY, "user4", "Comment 4")  // 2
        );
        // Average = (5+5+2+2)/4 = 3.5, which should map to JUST_NORMAL_REALLY
        
        when(moodRepository.findBySubmissionDateBetween(startOfDay, endOfDay))
                .thenReturn(submissions);
        
        // Act
        TeamMoodDTO result = moodService.getOverallMood();
        
        // Assert
        assertEquals(MoodType.JUST_NORMAL_REALLY, result.getOverallMood());
        assertEquals(4, result.getComments().size());
    }
}