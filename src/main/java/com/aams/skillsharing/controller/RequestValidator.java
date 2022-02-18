package com.aams.skillsharing.controller;

import com.aams.skillsharing.model.Request;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

public class RequestValidator implements Validator {
    @Override
    public boolean supports(@NotNull Class<?> aClass) {
        return Request.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(@NotNull Object o, @NotNull Errors errors) {
        Request request = (Request) o;

        LocalDate startDate = request.getStartDate();
        LocalDate finishDate = request.getFinishDate();
        if (finishDate.compareTo(startDate) <= 0) {
            errors.rejectValue("finishDate", "consistency",
                    "The finish date must be after the start date");
        }
    }
}
