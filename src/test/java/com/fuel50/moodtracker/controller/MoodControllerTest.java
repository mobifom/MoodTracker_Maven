package com.fuel50.moodtracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuel50.moodtracker.datatransferobject.MoodSubmissionDTO;
import com.fuel50.moodtracker.datatransferobject.TeamMoodDTO;
import com.fuel50.moodtracker.domainobject.MoodSubmissionDO;
import com.fuel50.moodtracker.domainvalue.MoodType;
import com.fuel50.moodtracker.exception.DuplicateMoodSubmissionException;
import com.fuel50.moodtracker.service.MoodService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MoodController.class)
public class MoodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MoodService moodService;

    @Test
    void testSubmitMood_Success() throws Exception {
        // Arrange
        MoodSubmissionDTO moodSubmission = new MoodSubmissionDTO(MoodType.HAPPY, null, "Test comment");
        doNothing().when(moodService).submitMood(any(MoodSubmissionDO.class));

        // Act & Assert
        mockMvc.perform(post("/api/mood")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moodSubmission)))
                .andExpect(status().isCreated());

        verify(moodService).submitMood(any(MoodSubmissionDO.class));
    }

    @Test
    void testSubmitMood_MissingMood() throws Exception {
        // Arrange
        MoodSubmissionDTO moodSubmission = new MoodSubmissionDTO(null, null, "Test comment");

        // Act & Assert
        mockMvc.perform(post("/api/mood")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moodSubmission)))
                .andExpect(status().isBadRequest());

        verify(moodService, never()).submitMood(any(MoodSubmissionDO.class));
    }

    @Test
    void testSubmitMood_WithExistingCookie() throws Exception {
        // Arrange
        MoodSubmissionDTO moodSubmission = new MoodSubmissionDTO(MoodType.HAPPY, null, "Test comment");
        doNothing().when(moodService).submitMood(any(MoodSubmissionDO.class));
        String userId = "test-user-id";

        // Act & Assert
        mockMvc.perform(post("/api/mood")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moodSubmission))
                .cookie(new javax.servlet.http.Cookie("user_id", userId)))
                .andExpect(status().isCreated());

        verify(moodService).submitMood(any(MoodSubmissionDO.class));
    }

    @Test
    void testSubmitMood_DuplicateSubmission() throws Exception {
        // Arrange
        MoodSubmissionDTO moodSubmission = new MoodSubmissionDTO(MoodType.HAPPY, null, "Test comment");
        doThrow(new DuplicateMoodSubmissionException("Already submitted"))
                .when(moodService).submitMood(any(MoodSubmissionDO.class));

        // Act & Assert
        mockMvc.perform(post("/api/mood")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moodSubmission)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Already submitted"));
    }

    @Test
    void testGetOverallTeamMood() throws Exception {
        // Arrange
        List<String> comments = Arrays.asList("Comment 1", "Comment 2");
        TeamMoodDTO teamMood = new TeamMoodDTO(MoodType.HAPPY, comments);
        when(moodService.getOverallMood()).thenReturn(teamMood);

        // Act & Assert
        MvcResult result = mockMvc.perform(get("/api/mood/overall"))
                .andExpect(status().isOk())
                .andReturn();

        TeamMoodDTO responseDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(), TeamMoodDTO.class);
        
        assertEquals(MoodType.HAPPY, responseDTO.getOverallMood());
        assertEquals(2, responseDTO.getComments().size());
        assertTrue(responseDTO.getComments().contains("Comment 1"));
        assertTrue(responseDTO.getComments().contains("Comment 2"));
    }

    @Test
    void testGetOverallTeamMood_NoMoods() throws Exception {
        // Arrange
        TeamMoodDTO teamMood = new TeamMoodDTO(null, List.of());
        when(moodService.getOverallMood()).thenReturn(teamMood);

        // Act & Assert
        MvcResult result = mockMvc.perform(get("/api/mood/overall"))
                .andExpect(status().isOk())
                .andReturn();

        TeamMoodDTO responseDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(), TeamMoodDTO.class);
        
        assertNull(responseDTO.getOverallMood());
        assertTrue(responseDTO.getComments().isEmpty());
    }
}