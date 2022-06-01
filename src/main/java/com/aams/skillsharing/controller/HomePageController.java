package com.aams.skillsharing.controller;

import com.aams.skillsharing.dao.*;
import com.aams.skillsharing.model.InternalUser;
import com.aams.skillsharing.model.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/homePage")
public class HomePageController extends RoleController{

    private CollaborationDao collaborationDao;
    private RequestDao requestDao;

    private OfferDao offerDao;

    @Autowired
    public void setCollaborationDao(CollaborationDao collaborationDao) {
        this.collaborationDao = collaborationDao;
    }

    @Autowired
    public void setRequestDao(RequestDao requestDao) {
        this.requestDao = requestDao;
    }

    @Autowired
    public void setOfferDao(OfferDao offerDao) {
        this.offerDao = offerDao;
    }

    @RequestMapping("list")
    public String listHomePageStudent(HttpSession session, Model model){
        if(session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "redirect:/login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");
        String username = user.getUsername();

        /*
        System.out.println("REQUEEESTSSS" + requestDao.fetchLastThreeRequests(username)); --> 14, 11, 10 para skp
        System.out.println("OFFERRSSS" + offerDao.fetchLastThreeOffers(username)); --> 17, 6, 4 para skp
        System.out.println("COLLAAAABBBSSS" + collaborationDao.fetchLastThreeCollabs(username)); --> (3,2), (4,6), (4,3) para skp
        */
//        System.out.println("quack");

/*
        model.addAttribute("collaborations", collaborationDao.getCollaborationsStudent(username));
        model.addAttribute("offers", offerDao.getOffersStudent(username));
        model.addAttribute("requests", requestDao.getRequestsStudent(username));
*/
        model.addAttribute("collaborations", collaborationDao.fetchLastThreeCollabs(username));
        model.addAttribute("offers", offerDao.fetchLastThreeOffers(username));
        model.addAttribute("requests", requestDao.fetchLastThreeRequests(username));
        model.addAttribute("student", username);
        return "homePage/list";
    }


}
