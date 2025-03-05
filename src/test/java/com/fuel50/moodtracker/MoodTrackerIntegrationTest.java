package com.fuel50.moodtracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuel50.moodtracker.dataaccessobject.MoodRepository;
import com.fuel50.moodtracker.datatransferobject.MoodSubmissionDTO;
import com.fuel50.moodtracker.datatransferobject.TeamMoodDTO;
import com.fuel50.moodtracker.domainobject.MoodSubmissionDO;
import com.fuel50.moodtracker.domainvalue.MoodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MoodTrackerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MoodRepository moodRepository;

    private String userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        
        // Clear any data from previous tests
        moodRepository.deleteAll();
    }

    @Test
    void testSubmitAndRetrieveMood() throws Exception {
        // 1. Submit a mood
        MoodSubmissionDTO moodSubmission = new MoodSubmissionDTO(MoodType.HAPPY, null, "Integration test comment");
        
        // Submit mood with generated cookie
        MvcResult submitResult = mockMvc.perform(post("/api/mood")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moodSubmission)))
                .andExpect(status().isCreated())
                .andReturn();
        
        // Extract cookie from response
        Cookie userIdCookie = submitResult.getResponse().getCookie("user_id");
        assertNotNull(userIdCookie, "User ID cookie should be set");
        
        // 2. Verify the mood is stored in the database
        List<MoodSubmissionDO> submissions = moodRepository.findAll();
        assertEquals(1, submissions.size(), "One mood submission should be stored");
        assertEquals(MoodType.HAPPY, submissions.get(0).getMood());
        assertEquals("Integration test comment", submissions.get(0).getComment());
        assertEquals(userIdCookie.getValue(), submissions.get(0).getUserId());
        
        // 3. Retrieve team mood
        MvcResult getResult = mockMvc.perform(get("/api/mood/overall"))
                .andExpect(status().isOk())
                .andReturn();
        
        TeamMoodDTO teamMood = objectMapper.readValue(getResult.getResponse().getContentAsString(), TeamMoodDTO.class);
        assertEquals(MoodType.HAPPY, teamMood.getOverallMood());
        assertEquals(1, teamMood.getComments().size());
        assertEquals("Integration test comment", teamMood.getComments().get(0));
    }

    @Test
    void testDuplicateSubmission() throws Exception {
        // 1. Set up a user with a cookie
        MoodSubmissionDTO firstMood = new MoodSubmissionDTO(MoodType.HAPPY, null, "First submission");
        
        // Submit first mood and get cookie
        MvcResult result = mockMvc.perform(post("/api/mood")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstMood)))
                .andExpect(status().isCreated())
                .andReturn();
        
        Cookie userIdCookie = result.getResponse().getCookie("user_id");
        assertNotNull(userIdCookie);
        
        // 2. Try to submit a second mood with the same user ID
        MoodSubmissionDTO secondMood = new MoodSubmissionDTO(MoodType.GRUMPY, null, "Second submission");
        
        mockMvc.perform(post("/api/mood")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondMood))
                .cookie(userIdCookie))
                .andExpect(status().isBadRequest());
        
        // 3. Verify only one submission exists in the database
        List<MoodSubmissionDO> submissions = moodRepository.findAll();
        assertEquals(1, submissions.size(), "Only one mood submission should be stored");
        assertEquals("First submission", submissions.get(0).getComment());
    }

    @Test
    void testMultipleUsersMoodAggregation() throws Exception {
        // 1. Submit mood from user 1
        MoodSubmissionDTO user1Mood = new MoodSubmissionDTO(MoodType.HAPPY, null, "User 1 comment");
        mockMvc.perform(post("/api/mood")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1Mood)))
                .andExpect(status().isCreated());
        
        // 2. Submit mood from user 2
        MoodSubmissionDTO user2Mood = new MoodSubmissionDTO(MoodType.GRUMPY, null, "User 2 comment");
        mockMvc.perform(post("/api/mood")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("user_id", UUID.randomUUID().toString())) // Different user
                .content(objectMapper.writeValueAsString(user2Mood)))
                .andExpect(status().isCreated());
        
        // 3. Submit mood from user 3
        MoodSubmissionDTO user3Mood = new MoodSubmissionDTO(MoodType.JUST_NORMAL_REALLY, null, "User 3 comment");
        mockMvc.perform(post("/api/mood")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("user_id", UUID.randomUUID().toString())) // Different user
                .content(objectMapper.writeValueAsString(user3Mood)))
                .andExpect(status().isCreated());
        
        // 4. Retrieve team mood
        MvcResult getResult = mockMvc.perform(get("/api/mood/overall"))
                .andExpect(status().isOk())
                .andReturn();
        
        TeamMoodDTO teamMood = objectMapper.readValue(getResult.getResponse().getContentAsString(), TeamMoodDTO.class);
        
        // With scores of 5 (HAPPY), 2 (GRUMPY), and 4 (JUST_NORMAL_REALLY)
        // Average = (5 + 2 + 4) / 3 = 3.67, which maps to JUST_NORMAL_REALLY
        assertEquals(MoodType.JUST_NORMAL_REALLY, teamMood.getOverallMood(), 
                "Overall mood should be JUST_NORMAL_REALLY based on average score");
        
        assertEquals(3, teamMood.getComments().size(), "All 3 comments should be included");
        assertTrue(teamMood.getComments().contains("User 1 comment"));
        assertTrue(teamMood.getComments().contains("User 2 comment"));
        assertTrue(teamMood.getComments().contains("User 3 comment"));
    }

    @Test
    void testGetOverallMood_NoSubmissions() throws Exception {
        // Act - get overall mood with no submissions
        MvcResult getResult = mockMvc.perform(get("/api/mood/overall"))
                .andExpect(status().isOk())
                .andReturn();
        
        // Parse response
        TeamMoodDTO teamMood = objectMapper.readValue(getResult.getResponse().getContentAsString(), TeamMoodDTO.class);
        
        // Assert
        assertNull(teamMood.getOverallMood(), "Overall mood should be null when no submissions exist");
        assertTrue(teamMood.getComments().isEmpty(), "Comments list should be empty");
    }
}