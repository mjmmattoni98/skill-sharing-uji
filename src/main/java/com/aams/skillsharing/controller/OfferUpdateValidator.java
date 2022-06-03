package com.aams.skillsharing.controller;

import com.aams.skillsharing.model.Offer;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

public class OfferUpdateValidator implements Validator {
    @Override
    public boolean supports(@NotNull Class<?> aClass) {
        return Offer.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(@NotNull Object o, @NotNull Errors errors) {
        Offer offer = (Offer) o;

        if (offer.getDescription().length() == 0 || offer.getDescription().trim().length() == 0)
            errors.rejectValue("description", "no description","Description is required");

        LocalDate startDate = offer.getStartDate();
        LocalDate finishDate = offer.getFinishDate();
        if (finishDate != null && finishDate.compareTo(startDate) <= 0)
            errors.rejectValue("finishDate", "consistency",
                    "The finish date must be after the start date");
    }
}
