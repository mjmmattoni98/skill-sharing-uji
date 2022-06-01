package com.aams.skillsharing.controller;

import com.aams.skillsharing.model.Collaboration;
import com.aams.skillsharing.model.CollaborationState;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.LinkedList;
import java.util.List;

public class CollaborationValidator implements Validator {
    @Override
    public boolean supports(@NotNull Class<?> aClass) {
        return Collaboration.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(@NotNull Object o, @NotNull Errors errors) {
        Collaboration collaboration = (Collaboration) o;

        if (collaboration.getAssessment() < 0 || collaboration.getAssessment() > 5)
            errors.rejectValue("assessment", "incorrect collaboration value",
                    "It must be between 0 and 5");

        if (collaboration.getState() != null) {
            List<String> states = new LinkedList<>();
            for (CollaborationState collaborationState : CollaborationState.values())
                states.add(collaborationState.getId());
            if (!states.contains(collaboration.getState()))
                errors.rejectValue("state", "incorrect state value",
                        "It must be: " + states);
        }
    }
}
