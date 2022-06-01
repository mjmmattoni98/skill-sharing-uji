package com.aams.skillsharing.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpSession;

import com.aams.skillsharing.dao.CollaborationDao;
import com.aams.skillsharing.dao.OfferDao;
import com.aams.skillsharing.dao.RequestDao;
import com.aams.skillsharing.dao.SkillDao;
import com.aams.skillsharing.model.*;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/offer")
public class OfferController extends RoleController{
    private OfferDao offerDao;
    private SkillDao skillDao;
    private RequestDao requestDao;
    private CollaborationDao collaborationDao;
    private static final OfferValidator validator = new OfferValidator();

    @Autowired
    public void setOfferDao(OfferDao offerDao) {
        this.offerDao = offerDao;
    }

    @Autowired
    public void setRequestDao(RequestDao requestDao) {
        this.requestDao = requestDao;
    }

    @Autowired
    public void setCollaborationDao(CollaborationDao collaborationDao) {
        this.collaborationDao = collaborationDao;
    }

    @Autowired
    public void setSkillDao(SkillDao skillDao) {
        this.skillDao = skillDao;
    }

/*    @RequestMapping("/list")
    public String listOffers(Model model) {
        List<Offer> offers = offerDao.getOffers();

        model.addAttribute("offers", offers);
        return "offer/list";
    }

    @RequestMapping("/list/collaborate/{id}")
    public String listOffersToCollaborate(HttpSession session, Model model, @PathVariable int id) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }

        Request request = requestDao.getRequest(id);
        List<Offer> offers = offerDao.getOffersSkill(request.getName());
        // Remove my offers and the offers that are already collaborating with the request
        offers.removeIf(offer -> offer.getUsername().equals(request.getUsername()) ||
                        collaborationDao.getCollaboration(offer.getId(), request.getId()) != null);


        model.addAttribute("request", request);
        model.addAttribute("offers", offers);
        return "offer/list";
    }

    @RequestMapping("/list/student/{username}")
    public String listOffersStudent(HttpSession session, Model model, @PathVariable String username) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        if (!user.getUsername().equals(username))
            throw new SkillSharingException("You cannot list offers of other students",
                    "AccesDenied", "../" + user.getUrlMainPage());

        List<Offer> offers = offerDao.getOffersStudent(username);

        model.addAttribute("offers", offers);
        model.addAttribute("student", username);
        return "offer/list";
    }

    @RequestMapping("/list/skill/{name}")
    public String listOffersSkill(Model model, @PathVariable String name) {
        List<Offer> offers = offerDao.getOffersSkill(name);

        model.addAttribute("offers", offers);
        model.addAttribute("skill", name);
        return "offer/list";
    }*/

    @RequestMapping("/paged_list")
    public String listOffersPaged(Model model, @RequestParam("page") Optional<Integer> page) {
        model.addAttribute("offer_filter", new OfferFilter());
        return getOffersPaged(model, page.orElse(0), "", "", "", "", null);
    }

    @PostMapping("/paged_list/username")
    public String postListOffersPagedByName(Model model, @ModelAttribute("offer_filter") OfferFilter offerFilter) {
        model.addAttribute("offer_filter", offerFilter);
        return getOffersPaged(model, 0, offerFilter.getUsername(), "", "", "", null);
    }

    @GetMapping("/paged_list/username")
    public String getListOffersPagedByName(Model model, @RequestParam("username") Optional<String> username,
                                             @RequestParam("page") Optional<Integer> page) {
        OfferFilter offerFilter = new OfferFilter();
        offerFilter.setUsername(username.orElse(""));
        model.addAttribute("offer_filter", offerFilter);
        return getOffersPaged(model, page.orElse(0), offerFilter.getUsername(), "", "", "", null);
    }

    @RequestMapping("/paged_list/student")
    public String listOffersStudentPaged(HttpSession session, Model model, @RequestParam("page") Optional<Integer> page) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        model.addAttribute("student", user.getUsername());
        model.addAttribute("offer_filter", new OfferFilter());
        return getOffersPaged(model, page.orElse(0), "", "", "", user.getUsername(), null);
    }

    @PostMapping("/paged_list/student/skill")
    public String postListOffersStudentPagedByName(HttpSession session, Model model, @ModelAttribute("offer_filter") OfferFilter offerFilter) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        model.addAttribute("student", user.getUsername());
        model.addAttribute("offer_filter", offerFilter);
        return getOffersPaged(model, 0, "", offerFilter.getSkill(), "", user.getUsername(), null);
    }

    @GetMapping("/paged_list/student/skill")
    public String getListOffersStudentPagedByName(HttpSession session, Model model, @RequestParam("name") Optional<String> name,
                                                    @RequestParam("page") Optional<Integer> page) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        model.addAttribute("student", user.getUsername());
        OfferFilter offerFilter = new OfferFilter();
        offerFilter.setSkill(name.orElse(""));
        model.addAttribute("offer_filter", offerFilter);
        return getOffersPaged(model, page.orElse(0), "", offerFilter.getSkill(), "", user.getUsername(), null);
    }

    @RequestMapping("/paged_list/skill/{name}")
    public String listOffersSkillPaged(Model model, @PathVariable String name, @RequestParam("page") Optional<Integer> page) {
        model.addAttribute("skill", name);
        model.addAttribute("offer_filter", new OfferFilter());
        return getOffersPaged(model, page.orElse(0), "", "", name, "", null);
    }

    @PostMapping("/paged_list/skill/{name}/username")
    public String postListOffersSkillPagedByName(@PathVariable String name, Model model,
                                                   @ModelAttribute("offer_filter") OfferFilter offerFilter) {
        model.addAttribute("skill", name);
        model.addAttribute("offer_filter", offerFilter);
        return getOffersPaged(model, 0, offerFilter.getUsername(), "", name, "", null);
    }

    @GetMapping("/paged_list/skill/{name}/username")
    public String getListOffersSkillPagedByName(Model model, @PathVariable String name,
                                                  @RequestParam("username") Optional<String> username,
                                                  @RequestParam("page") Optional<Integer> page) {
        model.addAttribute("skill", name);
        OfferFilter offerFilter = new OfferFilter();
        offerFilter.setUsername(username.orElse(""));
        model.addAttribute("request_filter", offerFilter);
        return getOffersPaged(model, page.orElse(0), offerFilter.getUsername(), "", name, "", null);
    }

    @RequestMapping("/paged_list/collaborate/{id}")
    public String listOffersCollaboratePaged(Model model, @PathVariable int id, @RequestParam("page") Optional<Integer> page) {
        Request request = requestDao.getRequest(id);
        model.addAttribute("request", request);
        model.addAttribute("offer_filter", new OfferFilter());
        return getOffersPaged(model, page.orElse(0), "", "", "", "", request);
    }

    @PostMapping("/paged_list/collaborate/{id}/username")
    public String postListOffersCollaboratePagedByRequest(@PathVariable int id, Model model,
                                                 @ModelAttribute("offer_filter") OfferFilter offerFilter) {
        Request request = requestDao.getRequest(id);
        model.addAttribute("request", request);
        model.addAttribute("offer_filter", offerFilter);
        return getOffersPaged(model, 0, offerFilter.getUsername(), "", "", "", request);
    }

    @GetMapping("/paged_list/collaborate/{id}/username")
    public String getListOffersCollaboratePagedByRequest(Model model, @PathVariable int id,
                                                @RequestParam("username") Optional<String> username,
                                                @RequestParam("page") Optional<Integer> page) {
        Request request = requestDao.getRequest(id);
        model.addAttribute("request", request);
        OfferFilter offerFilter = new OfferFilter();
        offerFilter.setUsername(username.orElse(""));
        model.addAttribute("request_filter", offerFilter);
        return getOffersPaged(model, page.orElse(0), offerFilter.getUsername(), "", "", "", request);
    }

    @NotNull
    private String getOffersPaged(Model model, int page, String username, String name, String skill, String student, Request request) {
        List<Offer> offers = null;
        if (skill.isEmpty() && student.isEmpty()) {
            model.addAttribute("username", username);
            if (username.equals("")) {
                offers = offerDao.getOffers();
            } else {
                offers = offerDao.getOffersByUsername(username);
            }
        }
        if (!skill.isEmpty()) {
            model.addAttribute("username", username);
            if (username.equals("")) {
                offers = offerDao.getOffersSkill(skill);
            } else {
                offers = offerDao.getOffersSkillByUsername(skill, username);
            }
        }
        if (!student.isEmpty()) {
            model.addAttribute("name", name);
            if (name.equals("")) {
                offers = offerDao.getOffersStudent(student);
            } else {
                offers = offerDao.getOffersStudentBySkill(student, name);
            }
        }
        if (request != null) {
            model.addAttribute("username", username);
            if (username.equals("")) {
                offers = offerDao.getOffersSkill(request.getName());
            } else {
                offers = offerDao.getOffersSkillByUsername(request.getName(), username);
            }
            // Remove my offers and the offers that are already collaborating with the request
            offers.removeIf(offer -> offer.getUsername().equals(request.getUsername()) ||
                    collaborationDao.getCollaboration(offer.getId(), request.getId()) != null);
        }


        List<List<Offer>> offersPaged = new ArrayList<>();
        int start = 0;
        int pageLength = 8;
        int end = pageLength;
        while (end < offers.size()) {
            offersPaged.add(new ArrayList<>(offers.subList(start, end)));
            start += pageLength;
            end += pageLength;
        }
        offersPaged.add(new ArrayList<>(offers.subList(start, offers.size())));
        model.addAttribute("offers_paged", offersPaged);

        int totalPages = offersPaged.size();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("page_numbers", pageNumbers);
        }

        model.addAttribute("selected_page", page);

        return "offer/paged_list";
    }

    @RequestMapping(value = "/add/{name}")
    public String addOfferSkill(HttpSession session, Model model, @PathVariable String name) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");
        
        Offer offer = new Offer();
        offer.setFromSkill(true);
        offer.setUsername(user.getUsername());
        offer.setName(name);
        model.addAttribute("offer", offer);
        return "offer/add";
    }

    @RequestMapping(value = "/add")
    public String addOffer(HttpSession session, Model model) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        Offer offer = new Offer();
        offer.setFromSkill(false);
        offer.setUsername(user.getUsername());
        model.addAttribute("offer", offer);
        model.addAttribute("skills", skillDao.getAvailableSkills());
        return "offer/add";
    }

    @PostMapping(value = "/add")
    public String processAddOffer(@ModelAttribute("offer") Offer offer, Model model,
                                  BindingResult bindingResult) {
        validator.validate(offer, bindingResult);
        if (bindingResult.hasErrors()) {
            if (!offer.isFromSkill())
                model.addAttribute("skills", skillDao.getAvailableSkills());
            return "offer/add";
        }
        try {
            offerDao.addOffer(offer);
        }
        catch (DataAccessException e){
            throw new SkillSharingException("Error accessing the database\n" + e.getMessage(),
                    "ErrorAccessingDatabase", "/");
        }
        return "redirect:list/student/" + offer.getUsername();
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

}
