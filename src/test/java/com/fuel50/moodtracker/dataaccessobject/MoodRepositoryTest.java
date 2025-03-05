package com.fuel50.moodtracker.dataaccessobject;

import com.fuel50.moodtracker.domainobject.MoodSubmissionDO;
import com.fuel50.moodtracker.domainvalue.MoodType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class MoodRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MoodRepository moodRepository;

    @Test
    void testFindBySubmissionDateBetween() {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        
        // Create submissions for today
        MoodSubmissionDO mood1 = new MoodSubmissionDO(MoodType.HAPPY, "user1", "Comment 1");
        mood1.setSubmissionDate(LocalDateTime.now());
        
        MoodSubmissionDO mood2 = new MoodSubmissionDO(MoodType.A_BIT_MEH, "user2", "Comment 2");
        mood2.setSubmissionDate(LocalDateTime.now());
        
        // Create submission for yesterday
        MoodSubmissionDO mood3 = new MoodSubmissionDO(MoodType.GRUMPY, "user3", "Comment 3");
        mood3.setSubmissionDate(LocalDateTime.now().minusDays(1));
        
        entityManager.persist(mood1);
        entityManager.persist(mood2);
        entityManager.persist(mood3);
        entityManager.flush();
        
        // Act
        List<MoodSubmissionDO> todaySubmissions = moodRepository.findBySubmissionDateBetween(startOfDay, endOfDay);
        
        // Assert
        assertEquals(2, todaySubmissions.size(), "Should return 2 submissions from today");
        assertTrue(todaySubmissions.stream().anyMatch(m -> m.getUserId().equals("user1")));
        assertTrue(todaySubmissions.stream().anyMatch(m -> m.getUserId().equals("user2")));
        assertFalse(todaySubmissions.stream().anyMatch(m -> m.getUserId().equals("user3")));
    }

    @Test
    void testExistsByUserIdAndSubmissionDateBetween() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        
        MoodSubmissionDO mood = new MoodSubmissionDO(MoodType.HAPPY, userId, "Test comment");
        mood.setSubmissionDate(LocalDateTime.now());
        entityManager.persist(mood);
        entityManager.flush();
        
        // Act & Assert
        assertTrue(moodRepository.existsByUserIdAndSubmissionDateBetween(userId, startOfDay, endOfDay), 
                "Should find submission for user today");
        
        assertFalse(moodRepository.existsByUserIdAndSubmissionDateBetween("non-existent-user", startOfDay, endOfDay), 
                "Should not find submission for non-existent user");
        
        // Check for yesterday
        LocalDate yesterday = today.minusDays(1);
        LocalDateTime startOfYesterday = yesterday.atStartOfDay();
        LocalDateTime endOfYesterday = yesterday.atTime(23, 59, 59);
        
        assertFalse(moodRepository.existsByUserIdAndSubmissionDateBetween(userId, startOfYesterday, endOfYesterday), 
                "Should not find submission for user yesterday");
    }

    @Test
    void testSaveAndRetrieveMoodSubmission() {
        // Arrange
        MoodSubmissionDO mood = new MoodSubmissionDO(MoodType.JUST_NORMAL_REALLY, "test-user", "Test comment");
        mood.setSubmissionDate(LocalDateTime.now());
        
        // Act
        MoodSubmissionDO savedMood = moodRepository.save(mood);
        MoodSubmissionDO retrievedMood = entityManager.find(MoodSubmissionDO.class, savedMood.getId());
        
        // Assert
        assertNotNull(retrievedMood);
        assertEquals(MoodType.JUST_NORMAL_REALLY, retrievedMood.getMood());
        assertEquals("test-user", retrievedMood.getUserId());
        assertEquals("Test comment", retrievedMood.getComment());
    }
}