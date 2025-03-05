package com.fuel50.moodtracker.domainobject;

import com.fuel50.moodtracker.domainvalue.MoodType;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
@Entity
@Table(name = "mood_submission")
public class MoodSubmissionDO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MoodType mood;

    @Column(length = 350)
    private String comment;

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime submissionDate = LocalDateTime.now();

    @Column(nullable = false)
    private String userId;

    public MoodSubmissionDO() {
    }
    public MoodSubmissionDO(MoodType mood, String userId, String comment) {
        this.mood = mood;
        this.userId = userId;
        this.comment = comment;
    }


    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public MoodType getMood() { return mood; }
    public void setMood(MoodType mood) { this.mood = mood; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }


}
