package com.aams.skillsharing.model;

import lombok.Data;

@Data
public class Collaboration {
    private int idOffer;
    private int idRequest;
    private int hours = 0;
    private AssessmentScore assessment;
    private CollaborationState state;
}
