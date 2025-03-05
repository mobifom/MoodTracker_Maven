package com.fuel50.moodtracker.domainvalue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class MoodTypeTest {

    @Test
    void testMoodTypeScores() {
        assertEquals(5, MoodType.HAPPY.getScore());
        assertEquals(4, MoodType.JUST_NORMAL_REALLY.getScore());
        assertEquals(3, MoodType.A_BIT_MEH.getScore());
        assertEquals(2, MoodType.GRUMPY.getScore());
        assertEquals(1, MoodType.STRESSED_OUT_NOT_A_HAPPY_CAMPER.getScore());
    }

    @ParameterizedTest
    @CsvSource({
        "5.0, HAPPY",
        "4.5, HAPPY",
        "4.499, JUST_NORMAL_REALLY",
        "4.0, JUST_NORMAL_REALLY",
        "3.5, JUST_NORMAL_REALLY",
        "3.499, A_BIT_MEH",
        "3.0, A_BIT_MEH",
        "2.5, A_BIT_MEH",
        "2.499, GRUMPY",
        "2.0, GRUMPY",
        "1.5, GRUMPY",
        "1.499, STRESSED_OUT_NOT_A_HAPPY_CAMPER",
        "1.0, STRESSED_OUT_NOT_A_HAPPY_CAMPER",
        "0.5, STRESSED_OUT_NOT_A_HAPPY_CAMPER"
    })
    void testFromScore(double score, MoodType expectedMoodType) {
        assertEquals(expectedMoodType, MoodType.fromScore(score));
    }

    @Test
    void testFromScore_ExtremeValues() {
        // Test with extreme values
        assertEquals(MoodType.HAPPY, MoodType.fromScore(10.0));
        assertEquals(MoodType.STRESSED_OUT_NOT_A_HAPPY_CAMPER, MoodType.fromScore(0.0));
        assertEquals(MoodType.STRESSED_OUT_NOT_A_HAPPY_CAMPER, MoodType.fromScore(-1.0));
    }

    @Test
    void testFromScore_BoundaryValues() {
        // Test boundary values
        assertEquals(MoodType.HAPPY, MoodType.fromScore(4.5));
        assertEquals(MoodType.JUST_NORMAL_REALLY, MoodType.fromScore(4.49999));
        assertEquals(MoodType.JUST_NORMAL_REALLY, MoodType.fromScore(3.5));
        assertEquals(MoodType.A_BIT_MEH, MoodType.fromScore(3.49999));
        assertEquals(MoodType.A_BIT_MEH, MoodType.fromScore(2.5));
        assertEquals(MoodType.GRUMPY, MoodType.fromScore(2.49999));
        assertEquals(MoodType.GRUMPY, MoodType.fromScore(1.5));
        assertEquals(MoodType.STRESSED_OUT_NOT_A_HAPPY_CAMPER, MoodType.fromScore(1.49999));
    }
    
    @Test
    void testEnumValues() {
        MoodType[] moodTypes = MoodType.values();
        
        assertEquals(5, moodTypes.length);
        assertEquals(MoodType.HAPPY, moodTypes[0]);
        assertEquals(MoodType.JUST_NORMAL_REALLY, moodTypes[1]);
        assertEquals(MoodType.A_BIT_MEH, moodTypes[2]);
        assertEquals(MoodType.GRUMPY, moodTypes[3]);
        assertEquals(MoodType.STRESSED_OUT_NOT_A_HAPPY_CAMPER, moodTypes[4]);
    }
    
    @Test
    void testValueOf() {
        assertEquals(MoodType.HAPPY, MoodType.valueOf("HAPPY"));
        assertEquals(MoodType.JUST_NORMAL_REALLY, MoodType.valueOf("JUST_NORMAL_REALLY"));
        assertEquals(MoodType.A_BIT_MEH, MoodType.valueOf("A_BIT_MEH"));
        assertEquals(MoodType.GRUMPY, MoodType.valueOf("GRUMPY"));
        assertEquals(MoodType.STRESSED_OUT_NOT_A_HAPPY_CAMPER, MoodType.valueOf("STRESSED_OUT_NOT_A_HAPPY_CAMPER"));
    }
    
    @Test
    void testValueOf_InvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> MoodType.valueOf("INVALID_MOOD"));
    }
    
    @ParameterizedTest
    @ValueSource(doubles = {4.5, 4.6, 4.99, 5.0, 10.0, 100.0})
    void testFromScore_HappyRange(double score) {
        assertEquals(MoodType.HAPPY, MoodType.fromScore(score));
    }
    
    @ParameterizedTest
    @ValueSource(doubles = {3.5, 3.6, 4.0, 4.49})
    void testFromScore_JustNormalRange(double score) {
        assertEquals(MoodType.JUST_NORMAL_REALLY, MoodType.fromScore(score));
    }
    
    @ParameterizedTest
    @ValueSource(doubles = {2.5, 2.6, 3.0, 3.49})
    void testFromScore_BitMehRange(double score) {
        assertEquals(MoodType.A_BIT_MEH, MoodType.fromScore(score));
    }
    
    @ParameterizedTest
    @ValueSource(doubles = {1.5, 1.6, 2.0, 2.49})
    void testFromScore_GrumpyRange(double score) {
        assertEquals(MoodType.GRUMPY, MoodType.fromScore(score));
    }
    
    @ParameterizedTest
    @ValueSource(doubles = {-10.0, 0.0, 0.5, 1.0, 1.49})
    void testFromScore_StressedOutRange(double score) {
        assertEquals(MoodType.STRESSED_OUT_NOT_A_HAPPY_CAMPER, MoodType.fromScore(score));
    }
}