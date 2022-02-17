package com.aams.skillsharing.controller;

import com.aams.skillsharing.model.InternalUser;

import javax.servlet.http.HttpSession;

public abstract class RoleController {
    protected static final boolean SKP_ROLE = true;
    protected static final boolean STUDENT_ROLE = false;

    protected InternalUser checkSession(HttpSession session, boolean isSkp) {
        if(session.getAttribute("user") == null) return null;

        InternalUser user = (InternalUser) session.getAttribute("user");

        if (user.isSkp() != isSkp) {
            System.out.println("The user can't access this page with the role " + (user.isSkp() ? "skp" : "student"));
            throw new SkillSharingException("You don't have access to this page because you aren't " + (isSkp ? "skp" : "student"),
                    "AccesDenied", "../" + user.getUrlMainPage());
        }

        return user;
    }
}
