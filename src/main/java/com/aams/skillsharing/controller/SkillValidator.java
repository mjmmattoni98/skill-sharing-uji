package com.aams.skillsharing.controller;

import com.aams.skillsharing.model.Skill;
import com.aams.skillsharing.model.SkillLevel;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.LinkedList;
import java.util.List;

public class SkillValidator implements Validator {
    @Override
    public boolean supports(@NotNull Class<?> aClass) {
        return Skill.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(@NotNull Object o, @NotNull Errors errors) {
        Skill skill = (Skill) o;

        List<String> skillLevels = new LinkedList<>();
        for(SkillLevel skillLevel : SkillLevel.values())
            skillLevels.add(skillLevel.getId());
        if (!skillLevels.contains(skill.getLevel().getId()))
            errors.rejectValue("level", "incorrect skill level value",
                    "It must be: " + skillLevels);
    }
}
