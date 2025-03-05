package com.fuel50.moodtracker.controller.mapper;

import com.fuel50.moodtracker.datatransferobject.MoodSubmissionDTO;
import com.fuel50.moodtracker.domainobject.MoodSubmissionDO;
import com.fuel50.moodtracker.domainvalue.MoodType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MoodMapperTest {

    @Test
    void testMakeMoodSubmissionDO() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        String comment = "Test comment";
        MoodSubmissionDTO dto = new MoodSubmissionDTO(MoodType.HAPPY, userId, comment);
        
        // Act
        MoodSubmissionDO result = MoodMapper.makeMoodSubmissionDO(dto);
        
        // Assert
        assertEquals(MoodType.HAPPY, result.getMood());
        assertEquals(userId, result.getUserId());
        assertEquals(comment, result.getComment());
    }

    @Test
    void testMakeMoodSubmissionDTO() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        String comment = "Test comment";
        MoodSubmissionDO domainObject = new MoodSubmissionDO(MoodType.JUST_NORMAL_REALLY, userId, comment);
        LocalDateTime now = LocalDateTime.now();
        domainObject.setSubmissionDate(now);
        
        // Act
        MoodSubmissionDTO result = MoodMapper.makeMoodSubmissionDTO(domainObject);
        
        // Assert
        assertEquals(MoodType.JUST_NORMAL_REALLY, result.getMood());
        assertEquals(userId, result.getUserId());
        assertEquals(comment, result.getComment());
    }

    @Test
    void testMakeMoodSubmissionDTOList() {
        // Arrange
        MoodSubmissionDO mood1 = new MoodSubmissionDO(MoodType.HAPPY, "user1", "Comment 1");
        MoodSubmissionDO mood2 = new MoodSubmissionDO(MoodType.A_BIT_MEH, "user2", "Comment 2");
        List<MoodSubmissionDO> domainObjects = Arrays.asList(mood1, mood2);
        
        // Act
        List<MoodSubmissionDTO> results = MoodMapper.makeMoodSubmissionDTOList(domainObjects);
        
        // Assert
        assertEquals(2, results.size());
        
        // Verify first DTO
        MoodSubmissionDTO dto1 = results.get(0);
        assertEquals(MoodType.HAPPY, dto1.getMood());
        assertEquals("user1", dto1.getUserId());
        assertEquals("Comment 1", dto1.getComment());
        
        // Verify second DTO
        MoodSubmissionDTO dto2 = results.get(1);
        assertEquals(MoodType.A_BIT_MEH, dto2.getMood());
        assertEquals("user2", dto2.getUserId());
        assertEquals("Comment 2", dto2.getComment());
    }

    @Test
    void testMoodMapperWithNullValues() {
        // Arrange - DTO with null comment
        MoodSubmissionDTO dtoWithNullComment = new MoodSubmissionDTO(MoodType.GRUMPY, "user-id", null);
        
        // Act
        MoodSubmissionDO result = MoodMapper.makeMoodSubmissionDO(dtoWithNullComment);
        
        // Assert
        assertEquals(MoodType.GRUMPY, result.getMood());
        assertEquals("user-id", result.getUserId());
        assertNull(result.getComment());
        
        // Arrange - DO with null comment
        MoodSubmissionDO doWithNullComment = new MoodSubmissionDO(MoodType.STRESSED_OUT_NOT_A_HAPPY_CAMPER, "another-user", null);
        
        // Act
        MoodSubmissionDTO dtoResult = MoodMapper.makeMoodSubmissionDTO(doWithNullComment);
        
        // Assert
        assertEquals(MoodType.STRESSED_OUT_NOT_A_HAPPY_CAMPER, dtoResult.getMood());
        assertEquals("another-user", dtoResult.getUserId());
        assertNull(dtoResult.getComment());
    }
}