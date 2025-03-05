package com.fuel50.moodtracker.controller;

import com.fuel50.moodtracker.controller.mapper.MoodMapper;
import com.fuel50.moodtracker.datatransferobject.MoodSubmissionDTO;
import com.fuel50.moodtracker.datatransferobject.TeamMoodDTO;
import com.fuel50.moodtracker.domainobject.MoodSubmissionDO;
import com.fuel50.moodtracker.service.MoodService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/mood")
@Tag(name = "Mood")
class MoodController {

    private final MoodService moodService;

    public MoodController(MoodService moodService) {
        this.moodService = moodService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void submitMood (
            @Valid @RequestBody MoodSubmissionDTO moodSubmission,
            @CookieValue(value = "user_id", required = false) String userId,
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



    @GetMapping("/overall")
    public ResponseEntity<TeamMoodDTO> getOverallTeamMood() {
        return ResponseEntity.ok(moodService.getOverallMood());
    }
}
