package com.aams.skillsharing.controller;

import com.aams.skillsharing.dao.EmailDao;
import com.aams.skillsharing.dao.StudentDao;
import com.aams.skillsharing.model.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/email")
public class EmailController extends RoleController{
    private EmailDao emailDao;
    private StudentDao studentDao;

    @Autowired
    public void setStudentDao(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    @Autowired
    public void setEmailDao(EmailDao e) {
        this.emailDao = e;
    }

    @RequestMapping("/paged_list")
    public String listEmailsPaged(HttpSession session, Model model, @RequestParam("page") Optional<Integer> page) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        Student student = studentDao.getStudent(user.getUsername());
        model.addAttribute("email_filter", new EmailFilter());
        model.addAttribute( "student", user.getUsername());
        return getStudentsPaged(model, page.orElse(0), "", student.getEmail());
    }

    @PostMapping("/paged_list/sender")
    public String postListEmailsPagedBySender(HttpSession session, Model model, @ModelAttribute("email_filter") EmailFilter emailFilter) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        Student student = studentDao.getStudent(user.getUsername());

        model.addAttribute("email_filter", emailFilter);
        return getStudentsPaged(model, 0, emailFilter.getEmail(), student.getEmail());
    }

    @GetMapping("/paged_list/sender")
    public String getListEmailsPagedBySender(HttpSession session, Model model, @RequestParam("sender") Optional<String> sender,
                                             @RequestParam("page") Optional<Integer> page) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        Student student = studentDao.getStudent(user.getUsername());

        EmailFilter emailFilter = new EmailFilter();
        emailFilter.setEmail(sender.orElse(""));
        model.addAttribute("email_filter", emailFilter);
        return getStudentsPaged(model, page.orElse(0), emailFilter.getEmail(), student.getEmail());
    }

    @NotNull
    private String getStudentsPaged(Model model, int page, String sender, String receiver) {
        List<Email> emails;
        model.addAttribute("sender", sender);
        if (sender.equals("")) {
            emails = emailDao.getEmails(receiver);
        } else {
            emails = emailDao.getEmailsBySender(receiver, sender);
        }
        Collections.sort(emails);

        List<List<Email>> emailsPaged = new ArrayList<>();
        int start = 0;
        int pageLength = 8;
        int end = pageLength;
        while (end < emails.size()) {
            emailsPaged.add(new ArrayList<>(emails.subList(start, end)));
            start += pageLength;
            end += pageLength;
        }
        emailsPaged.add(new ArrayList<>(emails.subList(start, emails.size())));
        model.addAttribute("emails_paged", emailsPaged);

        int totalPages = emailsPaged.size();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("page_numbers", pageNumbers);
        }

        model.addAttribute("selected_page", page);
        return "email/paged_list";
    }

/*    @RequestMapping("/list/{receiver}")
    public String listEmails(HttpSession session, Model model, @PathVariable String receiver) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        Student student = studentDao.getStudent(user.getUsername());
        if (!student.getEmail().equals(receiver)) {
            throw new SkillSharingException("You can only see your own emails",
                    "AccesDenied", "../" + user.getUrlMainPage());
        }

        model.addAttribute("emails", emailDAO.getEmails(receiver));
        return "email/list";
    }
*/
    @RequestMapping(value = "/delete/{id}")
    public String processDeleteSkill(HttpSession session, Model model, @PathVariable int id) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        Student student = studentDao.getStudent(user.getUsername());
        Email email = emailDao.getEmail(id);
        if (!student.getEmail().equals(email.getReceiver()))
            throw new SkillSharingException("You cannot delete emails of other students",
                    "AccesDenied", "../" + user.getUrlMainPage());
        emailDao.deleteEmail(id);
        return "redirect:../paged_list/";
    }
}
