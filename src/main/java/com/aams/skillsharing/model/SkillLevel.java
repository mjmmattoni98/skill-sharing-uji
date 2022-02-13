package com.aams.skillsharing.model;

public enum SkillLevel {
    EXPERT("expert"),
    AVERAGE("average"),
    LOW("low");

    private final String id;

    SkillLevel(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static SkillLevel fromId(String id) {
        for (SkillLevel level : values()) {
            if (level.getId().equals(id)) {
                return level;
            }
        }
        throw new IllegalStateException("Unexpected value: " + id);
    }
}
