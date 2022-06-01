package com.aams.skillsharing.controller;

import java.time.LocalDate;

import com.aams.skillsharing.model.Request;

import org.jetbrains.annotations.NotNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class RequestValidator implements Validator {
    @Override
    public boolean supports(@NotNull Class<?> aClass) {
        return Request.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(@NotNull Object o, @NotNull Errors errors) {
        Request request = (Request) o;

        if (request.getDescription().length() == 0 || request.getDescription().trim().length() == 0)
            errors.rejectValue("description", "no description","Description is required");

        LocalDate startDate = request.getStartDate();
        LocalDate finishDate = request.getFinishDate();
        if (finishDate != null && finishDate.compareTo(startDate) <= 0) 
            errors.rejectValue("finishDate", "consistency",
                    "The finish date must be after the start date");
        if (startDate.compareTo(LocalDate.now()) < 0)
            errors.rejectValue("startDate", "consistency",
                    "The start date must be today or after");
    }
}
