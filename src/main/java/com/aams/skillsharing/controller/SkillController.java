package com.aams.skillsharing.controller;

import com.aams.skillsharing.dao.SkillDao;
import com.aams.skillsharing.model.InternalUser;
import com.aams.skillsharing.model.Skill;
import com.aams.skillsharing.model.SkillLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/skill")
public class SkillController extends RoleController {
    private SkillDao skillDao;
    private static final SkillValidator validator = new SkillValidator();

    @Autowired
    public void setSkillDao(SkillDao skillDao) {
        this.skillDao = skillDao;
    }

    @RequestMapping("/list")
    public String listSkills(Model model) {
        List<Skill> skills = skillDao.getSkills();

        model.addAttribute("skills", skills);
        return "skill/list";
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
        List<String> skillLevels = new LinkedList<>();
        for(SkillLevel skillLevel : SkillLevel.values())
            skillLevels.add(skillLevel.getId());
        model.addAttribute("skillLevels", skillLevels);
        return "skill/add";
    }

    @PostMapping(value = "/add")
    public String processAddSkill(@ModelAttribute("skill") Skill skill,
                                 BindingResult bindingResult) {
        validator.validate(skill, bindingResult);
        if (bindingResult.hasErrors()) {
            return "skill/add";
        }
        try {
            skillDao.addSkill(skill);
        }
        catch (DataAccessException e){
            throw new SkillSharingException("Error accessing the database\n" + e.getMessage(),
                    "ErrorAccessingDatabase", "/");
        }
        return "redirect:list/";
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
        List<String> skillLevels = new LinkedList<>();
        for(SkillLevel skillLevel : SkillLevel.values())
            skillLevels.add(skillLevel.getId());
        model.addAttribute("skillLevels", skillLevels);
        return "skill/update";
    }

    @PostMapping(value = "/update")
    public String processUpdateSubmit(@ModelAttribute("skill") Skill skill,
                                      BindingResult bindingResult) {
        validator.validate(skill, bindingResult);
        if (bindingResult.hasErrors()) return "skill/update";
        skillDao.updateSkill(skill);
        return "redirect:list/";
    }

    @RequestMapping(value = "/delete/{name}")
    public String processDeleteSkill(HttpSession session, Model model, @PathVariable String name) {
        InternalUser user = checkSession(session, SKP_ROLE);
        if (user == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }

        skillDao.deleteSkill(name);
        return "redirect:../list/";
    }
}
