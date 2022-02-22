package com.aams.skillsharing.controller;

import com.aams.skillsharing.dao.*;
import com.aams.skillsharing.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/collaboration")
public class CollaborationController extends RoleController{
    private CollaborationDao collaborationDao;
    private OfferDao offerDao;
    private RequestDao requestDao;
    private StudentDao studentDao;
    private EmailDao emailDao;
    private static final CollaborationValidator validator = new CollaborationValidator();

    @Autowired
    public void setCollaborationDao(CollaborationDao collaborationDao) {
        this.collaborationDao = collaborationDao;
    }

    @Autowired
    public void setEmailDao(EmailDao emailDao) {
        this.emailDao = emailDao;
    }

    @Autowired
    public void setOfferDao(OfferDao offerDao) {
        this.offerDao = offerDao;
    }

    @Autowired
    public void setRequestDao(RequestDao requestDao) {
        this.requestDao = requestDao;
    }

    @Autowired
    public void setStudentDao(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    @RequestMapping("/list")
    public String listCollaborations(HttpSession session, Model model) {
        InternalUser user = checkSession(session, SKP_ROLE);
        if (user == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }

        model.addAttribute("collaborations", collaborationDao.getCollaborations());
        return "collaboration/list";
    }

    @RequestMapping("/list/{username}")
    public String listCollaborationsStudent(HttpSession session, Model model, @PathVariable String username) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }

        model.addAttribute("collaborations", collaborationDao.getCollaborationsStudent(username));
        model.addAttribute("student", username);
        return "collaboration/list";
    }

    @RequestMapping(value = "/add/{idOffer}/{idRequest}")
    public String addCollaboration(HttpSession session, Model model, @PathVariable int idOffer, @PathVariable int idRequest) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }

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
            Student student = studentDao.getStudent(offerDao.getOffer(collaboration.getIdOffer()).getUsername());
            Email email = new Email();
            email.setSender("skill.sharing@uji.es");
            email.setReceiver(student.getEmail());
            email.setSendDate(LocalDate.now());
            email.setSubject("New collaboration");
            email.setBody("You have a new collaboration with " + requestDao.getRequest(collaboration.getIdRequest()).getUsername());
            emailDao.addEmail(email);
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
        List<String> assessments = new LinkedList<>();
        for(AssessmentScore assessment : AssessmentScore.values())
            assessments.add(assessment.getId());
        model.addAttribute("assessments", assessments);
        List<String> states = new LinkedList<>();
        for(CollaborationState state : CollaborationState.values())
            states.add(state.getId());
        model.addAttribute("states", states);
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
