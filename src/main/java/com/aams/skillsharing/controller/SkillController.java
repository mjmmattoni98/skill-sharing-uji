package com.aams.skillsharing.controller;

import com.aams.skillsharing.dao.*;
import com.aams.skillsharing.model.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/skill")
public class SkillController extends RoleController {
    private SkillDao skillDao;
    private RequestDao requestDao;
    private OfferDao offerDao;
    private EmailDao emailDao;
    private StudentDao studentDao;
    private static final SkillValidator validator = new SkillValidator();

    @Autowired
    public void setSkillDao(SkillDao skillDao) {
        this.skillDao = skillDao;
    }

    @Autowired
    public void setRequestDao(RequestDao requestDao) {
        this.requestDao = requestDao;
    }

    @Autowired
    public void setOfferDao(OfferDao offerDao) {
        this.offerDao = offerDao;
    }

    @Autowired
    public void setEmailDao(EmailDao emailDao) {
        this.emailDao = emailDao;
    }

    @Autowired
    public void setStudentDao(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

/*    @RequestMapping("/list")
    public String listSkills(Model model) {
        model.addAttribute("skills", skillDao.getAvailableSkills());
        model.addAttribute("skills_disabled", skillDao.getDisabledSkills());
        return "skill/list";
    }
*/

    @RequestMapping("/paged_list")
    public String listSkillsPaged(Model model, @RequestParam("page") Optional<Integer> page) {
        model.addAttribute("skill_filter", new SkillFilter());
        return getSkillsPaged(model, page.orElse(0),"");
    }

    @PostMapping("/paged_list/name")
    public String postListSkillsPagedByName(Model model, @ModelAttribute("skill_filter") SkillFilter skillFilter) {
        model.addAttribute("skill_filter", skillFilter);
        return getSkillsPaged(model, 0, skillFilter.getName());
    }

    @GetMapping("/paged_list/name")
    public String getListSkillsPagedByName(Model model, @RequestParam("name") Optional<String> name,
                                        @RequestParam("page") Optional<Integer> page) {

        SkillFilter skillFilter = new SkillFilter();
        skillFilter.setName(name.orElse(""));
        model.addAttribute("skill_filter", skillFilter);
        return getSkillsPaged(model, page.orElse(0), skillFilter.getName());
    }

    @NotNull
    private String getSkillsPaged(Model model, int page, String name) {
        List<Skill> skills;
        List<Skill> skillsDisabled;
        model.addAttribute("name", name);
        if (name.equals("")) {
            skills = skillDao.getAvailableSkills();
            skillsDisabled = skillDao.getDisabledSkills();
        } else {
            skills = skillDao.getAvailableSkillsByName(name);
            skillsDisabled = skillDao.getDisabledSkillsByName(name);
        }
        Collections.sort(skills);
        Collections.sort(skillsDisabled);
        skills.addAll(skillsDisabled);

        List<List<Skill>> skillsPaged = new ArrayList<>();
        int start = 0;
        int pageLength = 8;
        int end = pageLength;
        while (end < skills.size()) {
            skillsPaged.add(new ArrayList<>(skills.subList(start, end)));
            start += pageLength;
            end += pageLength;
        }
        skillsPaged.add(new ArrayList<>(skills.subList(start, skills.size())));
        model.addAttribute("skills_paged", skillsPaged);

        int totalPages = skillsPaged.size();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("page_numbers", pageNumbers);
        }

        model.addAttribute("selected_page", page);

        return "skill/paged_list";
    }

    @RequestMapping(value = "/add")
    public String addSkill(HttpSession session, Model model) {
        InternalUser user = checkSession(session, SKP_ROLE);
        if (user == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }

        Skill skill = new Skill();
        model.addAttribute("skill", skill);
        model.addAttribute("skillLevels", loadSkills());
        return "skill/add";
    }

    @PostMapping(value = "/add")
    public String processAddSkill(@ModelAttribute("skill") Skill skill, Model model,
                                  BindingResult bindingResult) {

        if (!skillDao.getSkillsByName(skill.getName()).isEmpty())
            bindingResult.rejectValue("name", "duplicated", "This skill already exists");

        validator.validate(skill, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("skillLevels", loadSkills());
            return "skill/add";
        }
        try {
            skillDao.addSkill(skill);
        }
        catch (DataAccessException e){
            throw new SkillSharingException("Error accessing the database\n" + e.getMessage(),
                    "ErrorAccessingDatabase", "/");
        }
        return "redirect:paged_list/";
    }

    @GetMapping(value = "/update/{name}")
    public String updateSkill(HttpSession session, Model model, @PathVariable String name) {
        InternalUser user = checkSession(session, SKP_ROLE);
        if (user == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }

        Skill skill = skillDao.getSkill(name);
        model.addAttribute("skill", skill);
        model.addAttribute("skillLevels", loadSkills());
        return "skill/update";
    }

    @PostMapping(value = "/update")
    public String processUpdateSubmit(@ModelAttribute("skill") Skill skill, Model model,
                                      BindingResult bindingResult) {
        validator.validate(skill, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("skills", loadSkills());
            return "skill/update";
        }

        if (skill.isCanceled()){
            List<Offer> offers = offerDao.getOffersSkillNotCollaborating(skill.getName());
            for(Offer offer : offers){
                offer.setCanceled(true);
                offerDao.updateOffer(offer);

                Student student = studentDao.getStudent(offer.getUsername());
                Email email = new Email();
                email.setSender("skill.sharing@uji.es");
                email.setReceiver(student.getEmail());
                email.setSendDate(LocalDate.now());
                email.setSubject("Skill disabled");
                email.setBody("Due to the skill you were offering help has been disabled, you can no longer offer it.");
                emailDao.addEmail(email);
            }

            List<Request> requests = requestDao.getRequestsSkillNotCollaborating(skill.getName());
            for(Request request : requests){
                request.setCanceled(true);
                requestDao.updateRequest(request);

                Student student = studentDao.getStudent(request.getUsername());
                Email email = new Email();
                email.setSender("skill.sharing@uji.es");
                email.setReceiver(student.getEmail());
                email.setSendDate(LocalDate.now());
                email.setSubject("Skill disabled");
                email.setBody("Due to the skill you were requesting help has been disabled, you can no longer request it.");
                emailDao.addEmail(email);
            }    
        }

        skillDao.updateSkill(skill);
        return "redirect:paged_list";
    }

    @RequestMapping(value = "/activateDisable/{name}")
    public String activateOrDisableSkill(HttpSession session, Model model, @PathVariable String name) {
        InternalUser user = checkSession(session, SKP_ROLE);
        if (user == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }

        Skill skill = skillDao.getSkill(name);
        skill.setCanceled(!skill.isCanceled());
        if (skill.isCanceled()){
            List<Offer> offers = offerDao.getOffersSkillNotCollaborating(skill.getName());
            for(Offer offer : offers){
                offer.setCanceled(true);
                offerDao.updateOffer(offer);

                Student student = studentDao.getStudent(offer.getUsername());
                Email email = new Email();
                email.setSender("skill.sharing@uji.es");
                email.setReceiver(student.getEmail());
                email.setSendDate(LocalDate.now());
                email.setSubject("Skill disabled");
                email.setBody("Due to the skill you were offering help has been disabled, you can no longer offer it.");
                emailDao.addEmail(email);
            }

            List<Request> requests = requestDao.getRequestsSkillNotCollaborating(skill.getName());
            for(Request request : requests){
                request.setCanceled(true);
                requestDao.updateRequest(request);

                Student student = studentDao.getStudent(request.getUsername());
                Email email = new Email();
                email.setSender("skill.sharing@uji.es");
                email.setReceiver(student.getEmail());
                email.setSendDate(LocalDate.now());
                email.setSubject("Skill disabled");
                email.setBody("Due to the skill you were requesting help has been disabled, you can no longer request it.");
                emailDao.addEmail(email);
            }
        }
        skillDao.updateSkill(skill);
        model.addAttribute("skill_filter", new SkillFilter());
        return "redirect:../paged_list";
    }

    public List<String> loadSkills() {
        List<String> skillLevels = new LinkedList<>();
        for(SkillLevel skillLevel : SkillLevel.values())
            skillLevels.add(skillLevel.getId());
        return skillLevels;
    }

}
