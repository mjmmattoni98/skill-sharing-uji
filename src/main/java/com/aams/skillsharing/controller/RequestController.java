package com.aams.skillsharing.controller;

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

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/request")
public class RequestController extends RoleController {
    private RequestDao requestDao;
    private SkillDao skillDao;
    private static final RequestValidator validator = new RequestValidator();

    @Autowired
    public void setRequestDao(RequestDao requestDao) {
        this.requestDao = requestDao;
    }

    @Autowired
    public void setSkillDao(SkillDao skillDao) {
        this.skillDao = skillDao;
    }

    /*    @RequestMapping("/list")
        public String listRequests(Model model) {
            List<Request> requests = requestDao.getRequests();

            model.addAttribute("requests", requests);
            return "request/list";
        }

        @RequestMapping("/list/student")
        public String listRequestsStudent(HttpSession session, Model model) {
            if (session.getAttribute("user") == null){
                model.addAttribute("user", new InternalUser());
                return "login";
            }
            InternalUser user = (InternalUser) session.getAttribute("user");

            List<Request> requests = requestDao.getRequestsStudent(user.getUsername());

            model.addAttribute("requests", requests);
            model.addAttribute("student", user.getUsername());
            return "request/list";
        }

        @RequestMapping("/list/skill/{name}")
        public String listRequestsSkill(Model model, @PathVariable String name) {
            List<Request> requests = requestDao.getRequestsSkill(name);

            model.addAttribute("skill", name);
            model.addAttribute("requests", requests);
            return "request/list";
        }
    */
    @RequestMapping("/paged_list")
    public String listRequestsPaged(Model model, @RequestParam("page") Optional<Integer> page) {
        model.addAttribute("request_filter", new RequestFilter());
        return getRequestsPaged(model, page.orElse(0), "", "", "", "");
    }

    @PostMapping("/paged_list/username")
    public String postListRequestsPagedByName(Model model, @ModelAttribute("request_filter") RequestFilter requestFilter) {
        model.addAttribute("request_filter", requestFilter);
        return getRequestsPaged(model, 0, requestFilter.getUsername(), "", "", "");
    }

    @GetMapping("/paged_list/username")
    public String getListRequestsPagedByName(Model model, @RequestParam("username") Optional<String> username,
                                             @RequestParam("page") Optional<Integer> page) {
        RequestFilter requestFilter = new RequestFilter();
        requestFilter.setUsername(username.orElse(""));
        model.addAttribute("request_filter", requestFilter);
        return getRequestsPaged(model, page.orElse(0), requestFilter.getUsername(), "", "", "");
    }

    @RequestMapping("/paged_list/student")
    public String listRequestsStudentPaged(HttpSession session, Model model, @RequestParam("page") Optional<Integer> page) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        model.addAttribute("student", user.getUsername());
        model.addAttribute("request_filter", new RequestFilter());
        return getRequestsPaged(model, page.orElse(0), "", "", "", user.getUsername());
    }

    @PostMapping("/paged_list/student/skill")
    public String postListRequestsStudentPagedByName(HttpSession session, Model model, @ModelAttribute("request_filter") RequestFilter requestFilter) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        model.addAttribute("student", user.getUsername());
        model.addAttribute("request_filter", requestFilter);
        return getRequestsPaged(model, 0, "", requestFilter.getSkill(), "", user.getUsername());
    }

    @GetMapping("/paged_list/student/skill")
    public String getListRequestsStudentPagedByName(HttpSession session, Model model, @RequestParam("name") Optional<String> name,
                                                    @RequestParam("page") Optional<Integer> page) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        model.addAttribute("student", user.getUsername());
        RequestFilter requestFilter = new RequestFilter();
        requestFilter.setSkill(name.orElse(""));
        model.addAttribute("request_filter", requestFilter);
        return getRequestsPaged(model, page.orElse(0), "", requestFilter.getSkill(), "", user.getUsername());
    }

    @RequestMapping("/paged_list/skill/{name}")
    public String listRequestsSkillPaged(Model model, @PathVariable String name, @RequestParam("page") Optional<Integer> page) {
        model.addAttribute("skill", name);
        model.addAttribute("request_filter", new RequestFilter());
        return getRequestsPaged(model, page.orElse(0), "", "", name, "");
    }

    @PostMapping("/paged_list/skill/{name}/username")
    public String postListRequestsSkillPagedByName(@PathVariable String name, Model model,
                                                   @ModelAttribute("request_filter") RequestFilter requestFilter) {
        model.addAttribute("skill", name);
        model.addAttribute("request_filter", requestFilter);
        return getRequestsPaged(model, 0, requestFilter.getUsername(), "", name, "");
    }

    @GetMapping("/paged_list/skill/{name}/username")
    public String getListRequestsSkillPagedByName(Model model, @PathVariable String name,
                                                  @RequestParam("username") Optional<String> username,
                                                  @RequestParam("page") Optional<Integer> page) {
        model.addAttribute("skill", name);
        RequestFilter requestFilter = new RequestFilter();
        requestFilter.setUsername(username.orElse(""));
        model.addAttribute("request_filter", requestFilter);
        return getRequestsPaged(model, page.orElse(0), requestFilter.getUsername(), "", name, "");
    }

    @NotNull
    private String getRequestsPaged(Model model, int page, String username, String name, String skill, String student) {
        List<Request> requests = null;
        if (skill.isEmpty() && student.isEmpty()) {
            model.addAttribute("username", username);
            if (username.equals("")) {
                requests = requestDao.getRequests();
            } else {
                requests = requestDao.getRequestsByUsername(username);
            }
        }
        if (!skill.isEmpty()) {
            model.addAttribute("username", username);
            if (username.equals("")) {
                requests = requestDao.getRequestsSkill(skill);
            } else {
                requests = requestDao.getRequestsSkillByUsername(skill, username);
            }
        }
        if (!student.isEmpty()) {
            model.addAttribute("name", name);
            if (name.equals("")) {
                requests = requestDao.getRequestsStudent(student);
            } else {
                requests = requestDao.getRequestsStudentBySkill(student, name);
            }
        }

        List<List<Request>> requestsPaged = new ArrayList<>();
        int start = 0;
        int pageLength = 8;
        int end = pageLength;
        while (end < requests.size()) {
            requestsPaged.add(new ArrayList<>(requests.subList(start, end)));
            start += pageLength;
            end += pageLength;
        }
        requestsPaged.add(new ArrayList<>(requests.subList(start, requests.size())));
        model.addAttribute("requests_paged", requestsPaged);

        int totalPages = requestsPaged.size();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("page_numbers", pageNumbers);
        }

        model.addAttribute("selected_page", page);

        return "request/paged_list";
    }

    @RequestMapping(value = "/add/{name}")
    public String addRequestSkill(HttpSession session, Model model, @PathVariable String name) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        Request request = new Request();
        request.setFromSkill(true);
        request.setUsername(user.getUsername());
        request.setName(name);
        model.addAttribute("request", request);
        return "request/add";
    }

    @RequestMapping(value = "/add")
    public String addRequest(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        Request request = new Request();
        request.setFromSkill(false);
        request.setUsername(user.getUsername());
        model.addAttribute("request", request);
        model.addAttribute("skills", skillDao.getAvailableSkills());
        return "request/add";
    }

    @PostMapping(value = "/add")
    public String processAddRequest(@ModelAttribute("request") Request request, Model model,
                                    BindingResult bindingResult) {
        validator.validate(request, bindingResult);
        if (bindingResult.hasErrors()) {
            if (!request.isFromSkill())
                model.addAttribute("skills", skillDao.getAvailableSkills());
            return "request/add";
        }
        try {
            requestDao.addRequest(request);
        } catch (DataAccessException e) {
            throw new SkillSharingException("Error accessing the database\n" + e.getMessage(),
                    "ErrorAccessingDatabase", "/");
        }
        return "redirect:list/student/" + request.getUsername();
    }

    @GetMapping(value = "/update/{id}")
    public String updateRequest(HttpSession session, Model model, @PathVariable int id) {
        if (session.getAttribute("user") == null) {
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

}
