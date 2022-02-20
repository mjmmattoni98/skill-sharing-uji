package com.aams.skillsharing.controller;

import com.aams.skillsharing.model.Student;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

public class StudentValidator implements Validator {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALID_PASSWORD_REGEX = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    //    private static final String PATTERN = "^[_A-Za-z0-9-\+]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})$";

    @Override
    public boolean supports(@NotNull Class<?> aClass) {
        return Student.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(@NotNull Object o, @NotNull Errors errors) {
        Student student = (Student) o;

        if (student.getUsername().length() != 8)
            errors.rejectValue("username", "length", "You must submit your al username");
        if(student.getNumber() < 1)
            errors.rejectValue("number", "consistency", "Your number must be greater than 0");
        if(student.getPc() < 1)
            errors.rejectValue("pc", "consistency", "Your pc must be greater than 0");
        if(!VALID_EMAIL_ADDRESS_REGEX.matcher(student.getEmail()).matches())
            errors.rejectValue("email", "consistency", "Your email is not valid");
        if(!VALID_PASSWORD_REGEX.matcher(student.getPassword()).matches())
            errors.rejectValue("password", "consistency", "Your password must be at least 8 " +
                    "characters long and contain at least one number");
        if (!student.getPassword().equals(student.getConfirmPassword()))
            errors.rejectValue("confirmPassword", "consistency", "Your password and confirm " +
                    "password must be same");
    }
}
