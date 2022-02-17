package com.aams.skillsharing.controller;

import com.aams.skillsharing.model.InternalUser;

import javax.servlet.http.HttpSession;

public abstract class RoleController {
    protected static final String ROLE_STUDENT = "student";
    protected static final String ROLE_SKP = "skp";

    protected InternalUser checkSession(HttpSession session, String role){
        if(session.getAttribute("user") == null) return null;

        InternalUser user = (InternalUser) session.getAttribute("user");

        if (!user.isRole()) {
            System.out.println("The user can't access this page with the role ");
            throw new SkillSharingException("You don't have access to this page because you aren't " + role,
                    "AccesDenied", "../" + user.getUrlMainPage());
        }

        return user;
    }
}
