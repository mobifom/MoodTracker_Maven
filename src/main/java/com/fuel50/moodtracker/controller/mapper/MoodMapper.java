package com.fuel50.moodtracker.controller.mapper;

import com.fuel50.moodtracker.datatransferobject.MoodSubmissionDTO;
import com.fuel50.moodtracker.domainobject.MoodSubmissionDO;

import java.util.List;

public class MoodMapper {
    public static MoodSubmissionDO makeMoodSubmissionDO(MoodSubmissionDTO moodSubmissionDTO) {
        return new MoodSubmissionDO(moodSubmissionDTO.getMood(), moodSubmissionDTO.getUserId(), moodSubmissionDTO.getComment());
    }

    public static MoodSubmissionDTO makeMoodSubmissionDTO(MoodSubmissionDO moodSubmissionDO)
    {
        MoodSubmissionDTO.MoodSubmissionDTOBuilder moodSubmissionDTOBuilder = MoodSubmissionDTO.newBuilder()
                .setUserId(moodSubmissionDO.getUserId())
                .setMood(moodSubmissionDO.getMood())
                .setComment(moodSubmissionDO.getComment());

        return moodSubmissionDTOBuilder.createMoodSubmissionDTO();
    }


    public static List<MoodSubmissionDTO> makeMoodSubmissionDTOList(List<MoodSubmissionDO> todayMoods) {
        return todayMoods.stream().map(MoodMapper::makeMoodSubmissionDTO).toList();
    }
}
