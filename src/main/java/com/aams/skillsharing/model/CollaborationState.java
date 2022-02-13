package com.aams.skillsharing.model;

public enum CollaborationState {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String id;

    CollaborationState(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static CollaborationState fromId(String id) {
        for (CollaborationState state : values()) {
            if (state.getId().equals(id)) {
                return state;
            }
        }
        throw new IllegalStateException("Unexpected value: " + id);
    }
}