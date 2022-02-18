package com.aams.skillsharing.controller;

import com.aams.skillsharing.dao.CollaborationDao;
import com.aams.skillsharing.dao.OfferDao;
import com.aams.skillsharing.dao.RequestDao;
import com.aams.skillsharing.model.Collaboration;
import com.aams.skillsharing.model.InternalUser;
import com.aams.skillsharing.model.Offer;
import com.aams.skillsharing.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/collaboration")
public class CollaborationController extends RoleController{
    private CollaborationDao collaborationDao;
    private OfferDao offerDao;
    private RequestDao requestDao;
    private static final CollaborationValidator validator = new CollaborationValidator();

    @Autowired
    public void setCollaborationDao(CollaborationDao collaborationDao) {
        this.collaborationDao = collaborationDao;
    }

    @Autowired
    public void setOfferDao(OfferDao offerDao) {
        this.offerDao = offerDao;
    }

    @Autowired
    public void setRequestDao(RequestDao requestDao) {
        this.requestDao = requestDao;
    }

    @RequestMapping("/list")
    public String listCollaborations(HttpSession session, Model model) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        if (user.isSkp()) { // if user is a SKP
            model.addAttribute("collaborations", collaborationDao.getCollaborations());
        }
        else { // if user is a student
            model.addAttribute("collaborations", collaborationDao.getCollaborationsStudent(user.getUsername()));
        }

        return "collaboration/list";
    }

    @RequestMapping(value = "/add/{idOffer}/{idRequest}")
    public String addCollaboration(HttpSession session, Model model, @PathVariable int idOffer, @PathVariable int idRequest) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        Collaboration collaboration = new Collaboration();
        collaboration.setIdOffer(idOffer);
        collaboration.setIdRequest(idRequest);
        model.addAttribute("collaboration", collaboration);
        return "collaboration/add";
    }

    @PostMapping(value = "/add")
    public String processAddCollaboration(@ModelAttribute("collaboration") Collaboration collaboration,
                                  BindingResult bindingResult) {
        validator.validate(collaboration, bindingResult);
        if (bindingResult.hasErrors()) {
            return "collaboration/add";
        }
        try {
            collaborationDao.addCollaboration(collaboration);
        }
        catch (DataAccessException e){
            throw new SkillSharingException("Error accessing the database\n" + e.getMessage(),
                    "ErrorAccessingDatabase", "/");
        }
        return "redirect:list/";
    }

    @GetMapping(value = "/update/{idOffer}/{idRequest}")
    public String updateCollaboration(HttpSession session, Model model, @PathVariable int idOffer, @PathVariable int idRequest) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        Collaboration collaboration = collaborationDao.getCollaboration(idOffer, idRequest);
        Offer offer = offerDao.getOffer(idOffer);
        Request request = requestDao.getRequest(idRequest);
        if (!offer.getUsername().equals(user.getUsername()) && !request.getUsername().equals(user.getUsername()))
            throw new SkillSharingException("You are not allowed to update this collaboration", "NotAllowed", "/");
        model.addAttribute("collaboration", collaboration);
        return "collaboration/update";
    }

    @PostMapping(value = "/update")
    public String processUpdateSubmit(@ModelAttribute("collaboration") Collaboration collaboration,
                                      BindingResult bindingResult) {
        validator.validate(collaboration, bindingResult);
        if (bindingResult.hasErrors()) return "collaboration/update";
        collaborationDao.updateCollaboration(collaboration);
        return "redirect:list/";
    }
}
