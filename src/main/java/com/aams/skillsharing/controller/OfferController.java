package com.aams.skillsharing.controller;

import com.aams.skillsharing.dao.OfferDao;
import com.aams.skillsharing.model.InternalUser;
import com.aams.skillsharing.model.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/offer")
public class OfferController extends RoleController{
    private OfferDao offerDao;
    private static final OfferValidator validator = new OfferValidator();

    @Autowired
    public void setOfferDao(OfferDao offerDao) {
        this.offerDao = offerDao;
    }

    @RequestMapping("/list")
    public String lisOfferss(Model model) {
        List<Offer> requests = offerDao.getOffers();

        model.addAttribute("requests", requests);
        return "offer/list";
    }

    @RequestMapping("/list/{username}")
    public String lisOfferssStudent(HttpSession session, Model model, @PathVariable String username) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }

        InternalUser user = (InternalUser) session.getAttribute("user");
        if (user.isSkp()) { // if user is skp
            model.addAttribute("offer", offerDao.getOffersStudent(username));
        }
        else { // if user is student
            model.addAttribute("offer", offerDao.getOffersStudent(user.getUsername()));
        }

        return "offer/list";
    }

    @RequestMapping("/list/{name}")
    public String listOffersSkill(Model model, @PathVariable String name) {
        List<Offer> offers = offerDao.getOffersSkill(name);

        model.addAttribute("offer", offers);
        return "offer/list";
    }

    @RequestMapping(value = "/add/{name}")
    public String addOffer(HttpSession session, Model model, @PathVariable String name) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        Offer offer = new Offer();
        offer.setUsername(user.getUsername());
        offer.setName(name);
        model.addAttribute("offer", offer);
        return "offer/add";
    }

    @PostMapping(value = "/add")
    public String processAddOffer(@ModelAttribute("offer") Offer offer,
                                    BindingResult bindingResult) {
        validator.validate(offer, bindingResult);
        if (bindingResult.hasErrors()) {
            return "offer/add";
        }
        try {
            offerDao.addOffer(offer);
        }
        catch (DataAccessException e){
            throw new SkillSharingException("Error accessing the database\n" + e.getMessage(),
                    "ErrorAccessingDatabase", "/");
        }
        return "redirect:list/";
    }

    @GetMapping(value = "/update/{id}")
    public String updateOffer(HttpSession session, Model model, @PathVariable int id) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        Offer offer = offerDao.getOffer(id);
        if (!offer.getUsername().equals(user.getUsername()))
            throw new SkillSharingException("You are not allowed to update this offer", "NotAllowed", "/");
        model.addAttribute("offer", offer);
        return "offer/update";
    }

    @PostMapping(value = "/update")
    public String processUpdateSubmit(@ModelAttribute("offer") Offer offer,
                                      BindingResult bindingResult) {
        validator.validate(offer, bindingResult);
        if (bindingResult.hasErrors()) return "offer/update";
        offerDao.updateOffer(offer);
        return "redirect:list/";
    }

    @RequestMapping(value = "/delete/{id}")
    public String processDeleteOffer(HttpSession session, Model model, @PathVariable int id) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        Offer offer = offerDao.getOffer(id);
        if (user.isSkp() || offer.getUsername().equals(user.getUsername()))
            offerDao.deleteOffer(id);
        else
            throw new SkillSharingException("You are not allowed to update this offer", "NotAllowed", "/");

        return "redirect:../list/";
    }
}
