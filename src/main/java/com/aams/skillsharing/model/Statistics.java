package com.aams.skillsharing.model;

import lombok.Data;

import java.util.List;

@Data
public class Statistics {
    private Integer balanceHours;
    private Double avgAssesmentScore;
    private Double totalHours;
    private Double avgCollaborationHours;
    private Integer totalOffers;
    private Integer totalRequests;
    private Integer totalCollaborations;
    private List<String> skillsTakenPart;

    public Integer getBalanceHours() {
        return balanceHours;
    }

    public void setBalanceHours(Integer balanceHours) {
        this.balanceHours = balanceHours;
    }

    public Double getAvgAssesmentScore() {
        return avgAssesmentScore;
    }

    public void setAvgAssesmentScore(Double avgAssesmentScore) {
        this.avgAssesmentScore = avgAssesmentScore;
    }

    public Double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Double totalHours) {
        this.totalHours = totalHours;
    }

    public Double getAvgCollaborationHours() {
        return avgCollaborationHours;
    }

    public void setAvgCollaborationHours(Double avgCollaborationHours) {
        this.avgCollaborationHours = avgCollaborationHours;
    }

    public Integer getTotalOffers() {
        return totalOffers;
    }

    public void setTotalOffers(Integer totalOffers) {
        this.totalOffers = totalOffers;
    }

    public Integer getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Integer totalRequests) {
        this.totalRequests = totalRequests;
    }

    public Integer getTotalCollaborations() {
        return totalCollaborations;
    }

    public void setTotalCollaborations(Integer totalCollaborations) {
        this.totalCollaborations = totalCollaborations;
    }

    public List<String> getSkillsTakenPart() {
        return skillsTakenPart;
    }

    public void setSkillsTakenPart(List<String> skillsTakenPart) {
        this.skillsTakenPart = skillsTakenPart;
    }
}