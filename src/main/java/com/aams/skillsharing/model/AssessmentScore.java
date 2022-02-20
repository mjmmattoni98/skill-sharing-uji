package com.aams.skillsharing.model;

public enum AssessmentScore {
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5");

    private final String id;

    AssessmentScore(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static AssessmentScore fromId(String id) {
        for (AssessmentScore assessmentScore : values()) {
            if (assessmentScore.getId().equals(id)) {
                return assessmentScore;
            }
        }
        throw new IllegalStateException("Unexpected value: " + id);
    }
}