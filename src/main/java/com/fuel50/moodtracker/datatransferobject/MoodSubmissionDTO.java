package com.fuel50.moodtracker.datatransferobject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fuel50.moodtracker.domainvalue.MoodType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MoodSubmissionDTO {
    @NotNull(message = "Mood can not be null!")
    private MoodType mood;

    @JsonIgnore
    private String userId;

    // max length of 350 characters
    @Max(value = 350, message = "Comment can not be longer than 350 characters!")
    private String comment;

    private MoodSubmissionDTO() {
    }

    public static MoodSubmissionDTOBuilder newBuilder() {
        return new MoodSubmissionDTOBuilder();
    }


    public MoodSubmissionDTO(MoodType mood, String userId, String comment) {
        this.mood = mood;
        this.userId = userId;
        this.comment = comment;

    }

    public MoodType getMood() {
        return mood;
    }

    public void setMood(MoodType mood) {
        this.mood = mood;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public static class MoodSubmissionDTOBuilder {
        private MoodType mood;
        private String userId;
        private String comment;

        public MoodSubmissionDTOBuilder() {
        }

        public MoodSubmissionDTOBuilder setMood(MoodType mood) {
            this.mood = mood;
            return this;
        }

        public MoodSubmissionDTOBuilder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public MoodSubmissionDTOBuilder setComment(String comment) {
            this.comment = comment;
            return this;
        }

        public MoodSubmissionDTO createMoodSubmissionDTO() {
            return new MoodSubmissionDTO(mood, userId, comment);
        }
    }
}
