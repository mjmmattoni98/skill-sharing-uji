package com.aams.skillsharing.controller;

import java.util.LinkedList;
import java.util.List;

import com.aams.skillsharing.model.Skill;
import com.aams.skillsharing.model.SkillLevel;

import org.jetbrains.annotations.NotNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class SkillValidator implements Validator {
    @Override
    public boolean supports(@NotNull Class<?> aClass) {
        return Skill.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(@NotNull Object o, @NotNull Errors errors) {
        Skill skill = (Skill) o;

        if (skill.getName().length() == 0 || skill.getName().trim().length() == 0)
            errors.rejectValue("name", "no name","Name is required");

        if (skill.getDescription().length() == 0 || skill.getDescription().trim().length() == 0)
            errors.rejectValue("description", "no description","Description is required");

        List<String> skillLevels = new LinkedList<>();
        for(SkillLevel skillLevel : SkillLevel.values())
            skillLevels.add(skillLevel.getId());
        if (!skillLevels.contains(skill.getLevel()))
            errors.rejectValue("level", "incorrect skill level value",
                    "It must be: " + skillLevels);
    }
}
