package com.fuel50.moodtracker.service;

import com.fuel50.moodtracker.dataaccessobject.MoodRepository;
import com.fuel50.moodtracker.datatransferobject.TeamMoodDTO;
import com.fuel50.moodtracker.domainobject.MoodSubmissionDO;
import com.fuel50.moodtracker.domainvalue.MoodType;
import com.fuel50.moodtracker.exception.DuplicateMoodSubmissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefaultMoodService implements MoodService {
    private final MoodRepository moodRepository;
    private static final Logger LOG = LoggerFactory.getLogger(DefaultMoodService.class);


    public DefaultMoodService(MoodRepository moodRepository) {
        this.moodRepository = moodRepository;
    }

    public void submitMood(MoodSubmissionDO moodSubmission) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay(); // 12:00 AM
        LocalDateTime endOfDay = today.atTime(23, 59, 59); // 11:59:59 PM
        LOG.debug("+++++++++++++++++++ user : {}", moodSubmission.getUserId());

        if (moodRepository.existsByUserIdAndSubmissionDateBetween(moodSubmission.getUserId(), startOfDay, endOfDay)) {
            LOG.error("Duplicate mood submission for this user : {}", moodSubmission.getUserId());
            throw new DuplicateMoodSubmissionException("Sorry, you have already submitted your response for today, try again tomorrow!");
        }
        moodSubmission.setSubmissionDate(LocalDateTime.now());
        moodRepository.save(moodSubmission);
    }


    @Override
    public TeamMoodDTO getOverallMood() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay(); // 12:00 AM
        LocalDateTime endOfDay = today.atTime(23, 59, 59); // 11:59:59 PM

        List<MoodSubmissionDO> submissions = moodRepository.findBySubmissionDateBetween(startOfDay, endOfDay);
        if (submissions.isEmpty()) {
            return new TeamMoodDTO(null, Collections.emptyList());
        }
        double averageScore = submissions.stream()
                .mapToInt(submission -> submission.getMood().getScore())
                .average()
                .orElse(0.0);

        MoodType overallMood = MoodType.fromScore(averageScore);

        // Extract today's comments
        List<String> comments = submissions.stream()
                .map(MoodSubmissionDO::getComment)
                .filter(Objects::nonNull) // Ignore null messages
                .collect(Collectors.toList());

        return new TeamMoodDTO(overallMood, comments);

    }

}
