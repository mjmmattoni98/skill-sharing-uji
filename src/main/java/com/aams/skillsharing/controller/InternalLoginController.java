package com.aams.skillsharing.controller;

import com.aams.skillsharing.dao.UserDao;
import com.aams.skillsharing.model.InternalUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
public class InternalLoginController {
    private UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @RequestMapping("/login")
    public String login(Model model, HttpSession session) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");
        throw new SkillSharingException("You are already log in", "AlreadyLogIn", "../" + user.getUrlMainPage());
    }

    @PostMapping(value="/login")
    public String checkLogin(@ModelAttribute("user") InternalUser user,
                             BindingResult bindingResult, HttpSession session) {
        InternalUserValidator userValidator = new InternalUserValidator();
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "login";
        }
        // Comprobar que el login es el correcto intentando cargar el usuario
        user = userDao.loadUserByUsername(user.getUser(), user.getPassword());
        if (user == null) {
            bindingResult.rejectValue("password", "badpw", "Contrasenya incorrecta");
            return "login";
        }
        // Autenticado correctamente. Guardamos los datos en la sesión
        session.setAttribute("user", user);

        String nextUrl = user.getUrlMainPage();

        return "redirect:/" + nextUrl;
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}

class InternalUserValidator implements Validator {
    @Override
    public boolean supports(@NotNull Class<?> cls) {
        return InternalUser.class.isAssignableFrom(cls);
    }

    @Override
    public void validate(@NotNull Object o, @NotNull Errors errors) {

        InternalUser user = (InternalUser) o;
        if (user.getUser().length() == 0) {
            errors.rejectValue("user", "empty user field", "Introduce a user");
        }
        if (user.getPassword().length() == 0) {
            errors.rejectValue("password", "empty password field", "Introduce a password");
        }
    }
}
