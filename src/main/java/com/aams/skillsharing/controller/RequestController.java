package com.aams.skillsharing.controller;

import com.aams.skillsharing.dao.RequestDao;
import com.aams.skillsharing.model.InternalUser;
import com.aams.skillsharing.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/request")
public class RequestController extends RoleController{
    private RequestDao requestDao;
    private static final RequestValidator validator = new RequestValidator();

    @Autowired
    public void setRequestDao(RequestDao requestDao) {
        this.requestDao = requestDao;
    }

    @RequestMapping("/list")
    public String listRequests(Model model) {
        List<Request> requests = requestDao.getRequests();

        model.addAttribute("requests", requests);
        return "request/list";
    }

    @RequestMapping("/list/{username}")
    public String listRequestsStudent(HttpSession session, Model model, @PathVariable String username) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }

        InternalUser user = (InternalUser) session.getAttribute("user");
        if (user.isSkp()) { // if user is skp
            model.addAttribute("requests", requestDao.getRequestsStudent(username));
        }
        else { // if user is student
            model.addAttribute("requests", requestDao.getRequestsStudent(user.getUsername()));
        }

        return "request/list";
    }

    @RequestMapping("/list/{name}")
    public String listRequestsSkill(Model model, @PathVariable String name) {
        List<Request> requests = requestDao.getRequestsSkill(name);

        model.addAttribute("requests", requests);
        return "request/list";
    }

    @RequestMapping(value = "/add/{name}")
    public String addRequest(HttpSession session, Model model, @PathVariable String name) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        Request request = new Request();
        request.setUsername(user.getUsername());
        request.setName(name);
        model.addAttribute("request", request);
        return "request/add";
    }

    @PostMapping(value = "/add")
    public String processAddRequest(@ModelAttribute("request") Request request,
                                  BindingResult bindingResult) {
        validator.validate(request, bindingResult);
        if (bindingResult.hasErrors()) {
            return "request/add";
        }
        try {
            requestDao.addRequest(request);
        }
        catch (DataAccessException e){
            throw new SkillSharingException("Error accessing the database\n" + e.getMessage(),
                    "ErrorAccessingDatabase", "/");
        }
        return "redirect:list/";
    }

    @GetMapping(value = "/update/{id}")
    public String updateRequest(HttpSession session, Model model, @PathVariable int id) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        Request request = requestDao.getRequest(id);
        if (!request.getUsername().equals(user.getUsername()))
            throw new SkillSharingException("You are not allowed to update this request", "NotAllowed", "/");
        model.addAttribute("request", request);
        return "request/update";
    }

    @PostMapping(value = "/update")
    public String processUpdateSubmit(@ModelAttribute("request") Request request,
                                      BindingResult bindingResult) {
        validator.validate(request, bindingResult);
        if (bindingResult.hasErrors()) return "request/update";
        requestDao.updateRequest(request);
        return "redirect:list/";
    }

    @RequestMapping(value = "/delete/{id}")
    public String processDeleteRequest(HttpSession session, Model model, @PathVariable int id) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        Request request = requestDao.getRequest(id);
        if (user.isSkp() || request.getUsername().equals(user.getUsername()))
            requestDao.deleteRequest(id);
        else
            throw new SkillSharingException("You are not allowed to update this request", "NotAllowed", "/");

        return "redirect:../list/";
    }
}
