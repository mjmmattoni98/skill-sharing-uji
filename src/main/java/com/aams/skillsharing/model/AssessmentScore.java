package com.aams.skillsharing.model;

public enum AssessmentScore {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int id;

    AssessmentScore(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static AssessmentScore fromId(int id) {
        for (AssessmentScore assessmentScore : values()) {
            if (assessmentScore.getId() == id) {
                return assessmentScore;
            }
        }
        throw new IllegalStateException("Unexpected value: " + id);
    }
}