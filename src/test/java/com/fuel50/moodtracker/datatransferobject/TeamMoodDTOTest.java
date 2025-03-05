package com.fuel50.moodtracker.datatransferobject;

import com.fuel50.moodtracker.domainvalue.MoodType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TeamMoodDTOTest {

    @Test
    void testTeamMoodDTO_WithValues() {
        // Arrange
        MoodType mood = MoodType.HAPPY;
        List<String> comments = Arrays.asList("Comment 1", "Comment 2", "Comment 3");
        
        // Act
        TeamMoodDTO teamMood = new TeamMoodDTO(mood, comments);
        
        // Assert
        assertEquals(mood, teamMood.getOverallMood());
        assertEquals(3, teamMood.getComments().size());
        assertEquals("Comment 1", teamMood.getComments().get(0));
        assertEquals("Comment 2", teamMood.getComments().get(1));
        assertEquals("Comment 3", teamMood.getComments().get(2));
    }

    @Test
    void testTeamMoodDTO_WithNullMood() {
        // Arrange
        List<String> comments = Collections.singletonList("Comment 1");
        
        // Act
        TeamMoodDTO teamMood = new TeamMoodDTO(null, comments);
        
        // Assert
        assertNull(teamMood.getOverallMood());
        assertEquals(1, teamMood.getComments().size());
        assertEquals("Comment 1", teamMood.getComments().get(0));
    }

    @Test
    void testTeamMoodDTO_WithEmptyComments() {
        // Arrange
        MoodType mood = MoodType.GRUMPY;
        List<String> comments = Collections.emptyList();
        
        // Act
        TeamMoodDTO teamMood = new TeamMoodDTO(mood, comments);
        
        // Assert
        assertEquals(mood, teamMood.getOverallMood());
        assertTrue(teamMood.getComments().isEmpty());
    }

    @Test
    void testTeamMoodDTO_WithNullComments() {
        // Arrange & Act
        TeamMoodDTO teamMood = new TeamMoodDTO(MoodType.A_BIT_MEH, null);
        
        // Assert
        assertEquals(MoodType.A_BIT_MEH, teamMood.getOverallMood());
        assertNull(teamMood.getComments(), "Comments should be null when initialized with null");
    }
}