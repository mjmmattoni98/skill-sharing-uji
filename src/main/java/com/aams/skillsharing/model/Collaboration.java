package com.aams.skillsharing.model;

import lombok.Data;

@Data
public class Collaboration {
    private int idOffer;
    private String studentOffer;
    private String studentRequest;
    private int idRequest;
    private int hours = 0;
    private int assessment = 1;
    private CollaborationState state = CollaborationState.ACTIVE;
    private String skill;

    public void setState(String state) {
        this.state = CollaborationState.fromId(state);
    }

    public String getState() {
        if (this.state == null)
            return null;
        return this.state.getId();
    }
}
