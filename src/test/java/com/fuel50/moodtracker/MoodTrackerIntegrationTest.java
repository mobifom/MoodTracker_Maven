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
        MoodSubmissionDTO moodSubmission = MoodSubmissionDTO.newBuilder()
                .setMood(MoodType.HAPPY)
                .setComment("Integration test comment")
                .createMoodSubmissionDTO();
        
        // Convert to JSON and print for debugging
        String requestJson = objectMapper.writeValueAsString(moodSubmission);
        System.out.println("Request JSON: " + requestJson);
        
        // Perform the request but DON'T expect any status yet
        MvcResult submitResult = mockMvc.perform(post("/api/mood")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andReturn();
        
        // Get the actual response status and body
        int status = submitResult.getResponse().getStatus();
        String responseBody = submitResult.getResponse().getContentAsString();
        
        // Print debug information
        System.out.println("Response Status: " + status);
        System.out.println("Response Body: " + responseBody);
        
        // Now assert the status
        assertEquals(201, status, "Expected status 201 but got " + status + " with body: " + responseBody);
        
        // Continue with the rest of the test only if we got the expected status
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
        MoodSubmissionDTO firstMood = MoodSubmissionDTO.newBuilder()
                .setMood(MoodType.HAPPY)
                .setComment("First submission")
                .createMoodSubmissionDTO();
        
        String requestJson = objectMapper.writeValueAsString(firstMood);
        System.out.println("First request JSON: " + requestJson);
        
        // Submit first mood and get cookie
        MvcResult result = mockMvc.perform(post("/api/mood")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(r -> {
                    if (r.getResponse().getStatus() != 201) {
                        System.out.println("Response status: " + r.getResponse().getStatus());
                        System.out.println("Response body: " + r.getResponse().getContentAsString());
                    }
                })
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
        MoodSubmissionDTO user1Mood = MoodSubmissionDTO.newBuilder()
            .setMood(MoodType.HAPPY)
            .setComment("User 1 comment")
            .createMoodSubmissionDTO();

        String requestJson1 = objectMapper.writeValueAsString(user1Mood);
        System.out.println("User 1 request JSON: " + requestJson1);

        mockMvc.perform(post("/api/mood")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson1))
                .andDo(r -> {
                    if (r.getResponse().getStatus() != 201) {
                        System.out.println("Response status: " + r.getResponse().getStatus());
                        System.out.println("Response body: " + r.getResponse().getContentAsString());
                    }
                })
                .andExpect(status().isCreated());

        // 2. Submit mood from user 2 (Adding the missing user 2)
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