package com.fuel50.moodtracker.datatransferobject;

import com.fuel50.moodtracker.domainvalue.MoodType;

import java.util.List;

public class TeamMoodDTO {
    private final MoodType overallMood;
    private final List<String> comments;

    public TeamMoodDTO(MoodType overallMood, List<String> comments) {
        this.overallMood = overallMood;
        this.comments = comments;
    }

    // Getters
    public MoodType getOverallMood() { return overallMood; }
    public List<String> getComments() { return comments; }
}