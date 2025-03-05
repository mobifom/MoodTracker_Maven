package com.fuel50.moodtracker.dataaccessobject;

import com.fuel50.moodtracker.domainobject.MoodSubmissionDO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MoodRepository extends JpaRepository<MoodSubmissionDO, Long> {
    List<MoodSubmissionDO> findBySubmissionDateBetween(LocalDateTime start, LocalDateTime end);
    boolean existsByUserIdAndSubmissionDateBetween(String userId, LocalDateTime start, LocalDateTime end);

}
