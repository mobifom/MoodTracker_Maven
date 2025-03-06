package com.fuel50.moodtracker.controller;

import com.fuel50.moodtracker.controller.mapper.MoodMapper;
import com.fuel50.moodtracker.datatransferobject.MoodSubmissionDTO;
import com.fuel50.moodtracker.datatransferobject.TeamMoodDTO;
import com.fuel50.moodtracker.domainobject.MoodSubmissionDO;
import com.fuel50.moodtracker.service.MoodService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


/**
 * REST controller for managing mood submissions and retrieving mood analytics.
 * This controller provides endpoints for team members to submit their daily mood
 * and retrieve aggregate team mood information.
 */
@RestController
@RequestMapping("/api/mood")
@Tag(name = "Mood")
class MoodController {

    private final MoodService moodService;

    public MoodController(MoodService moodService) {
        this.moodService = moodService;
    }
    
    
    /**
     * Submits a new mood entry for the current user. Each user is identified by a cookie
     * and can only submit one mood per day. If the user doesn't have an ID cookie yet,
     * a new unique ID is generated and set in a cookie.
     *
     * @param moodSubmission The mood submission data containing mood level and optional comment
     * @param userId The user's ID from cookie (if exists)
     * @param response HTTP response used to set the user ID cookie if needed
     * @throws com.fuel50.moodtracker.exception.DuplicateMoodSubmissionException if the user has already submitted a mood today
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Submit a mood entry",
        description = "Allows a user to submit their mood for the day. Each user can only submit one mood per day."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Mood submitted successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input or mood already submitted today",
            content = @Content(
                mediaType = "text/plain",
                schema = @Schema(implementation = String.class, example = "Sorry, you have already submitted your response for today, try again tomorrow!")
            )
        )
    })
    public void submitMood (
            @Valid @RequestBody MoodSubmissionDTO moodSubmission,
            @RequestHeader(value = "X-User-Id", required = false)  String userId,
            HttpServletResponse response
    ) {
        if (userId == null) {
            // Generate a new unique user ID and set it in a cookie
            userId = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("user_id", userId);
            cookie.setPath("/");
            // set max age to only one day
            cookie.setMaxAge(60 * 60 * 24);
            response.addCookie(cookie);
        }
        
        // Store the user ID in the mood submission
        moodSubmission.setUserId(userId);

        MoodSubmissionDO moodSubmissionDO = MoodMapper.makeMoodSubmissionDO(moodSubmission);
        moodService.submitMood(moodSubmissionDO);
    }



    /**
     * Retrieves the overall team mood calculated from all submissions for the current day.
     * The overall mood is determined by averaging individual mood scores and mapping
     * the result to a MoodType. All non-null comments from the day's submissions are
     * also included in the response.
     *
     * @return A ResponseEntity containing the team mood DTO with overall mood and comments
     */
    @GetMapping("/overall")
    @Operation(summary = "Get team's overall mood", 
              description = "Retrieves the aggregated team mood for the current day, calculated as an average of all submissions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved team mood", 
                    content = @Content(schema = @Schema(implementation = TeamMoodDTO.class)))
    })
    public ResponseEntity<TeamMoodDTO> getOverallTeamMood() {
        return ResponseEntity.ok(moodService.getOverallMood());
    }
}
