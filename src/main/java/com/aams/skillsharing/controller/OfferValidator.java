package com.aams.skillsharing.controller;

import com.aams.skillsharing.model.Offer;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

public class OfferValidator implements Validator {
    @Override
    public boolean supports(@NotNull Class<?> aClass) {
        return Offer.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(@NotNull Object o, @NotNull Errors errors) {
        Offer offer = (Offer) o;

        LocalDate startDate = offer.getStartDate();
        LocalDate finishDate = offer.getFinishDate();
        if (finishDate.compareTo(startDate) <= 0) {
            errors.rejectValue("finishDate", "consistency",
                    "The finish date must be after the start date");
        }
    }
}
