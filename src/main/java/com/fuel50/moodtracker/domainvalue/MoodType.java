package com.fuel50.moodtracker.domainvalue;

public enum MoodType {
    HAPPY(5),
    JUST_NORMAL_REALLY(4),
    A_BIT_MEH(3),
    GRUMPY(2),
    STRESSED_OUT_NOT_A_HAPPY_CAMPER(1);

    private final int score;

    MoodType(int score) {
        this.score = score;
    }


    public int getScore() {
        return score;
    }

    public static MoodType fromScore(double averageScore) {
        if (averageScore >= 4.5) return HAPPY;
        if (averageScore >= 3.5) return JUST_NORMAL_REALLY;
        if (averageScore >= 2.5) return A_BIT_MEH;
        if (averageScore >= 1.5) return GRUMPY;
        return STRESSED_OUT_NOT_A_HAPPY_CAMPER;
    }
}


