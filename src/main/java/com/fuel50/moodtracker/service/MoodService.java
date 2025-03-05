package com.fuel50.moodtracker.service;

import com.fuel50.moodtracker.datatransferobject.TeamMoodDTO;
import com.fuel50.moodtracker.domainobject.MoodSubmissionDO;

import java.util.List;

public interface MoodService {
     void submitMood(MoodSubmissionDO moodSubmission);
     TeamMoodDTO getOverallMood();
}
