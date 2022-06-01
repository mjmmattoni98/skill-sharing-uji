package com.aams.skillsharing.controller;

import com.aams.skillsharing.dao.*;
import com.aams.skillsharing.model.*;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
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
@RequestMapping("/student")
public class StudentController extends RoleController {
    private StudentDao studentDao;
    private CollaborationDao collaborationDao;
    private OfferDao offerDao;
    private RequestDao requestDao;
    private EmailDao emailDao;
    private SkillDao skillDao;
    private static final StudentValidator validator = new StudentValidator();
    private static final StudentUpdateValidator updateValidator = new StudentUpdateValidator();
    private static final BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();

    @Autowired
    public void setStudentDao(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

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

    @Autowired
    public void setEmailDao(EmailDao emailDao) {
        this.emailDao = emailDao;
    }

    @Autowired
    public void setSkillDao(SkillDao skillDao) { this.skillDao = skillDao; }

    @RequestMapping("/paged_list")
    public String listStudentsPaged(HttpSession session, Model model, @RequestParam("page") Optional<Integer> page) {
        InternalUser user = checkSession(session, SKP_ROLE);
        if (user == null) {
            model.addAttribute("user", new InternalUser());
            return "login";
        }

        model.addAttribute("student_filter", new StudentFilter());
        return getStudentsPaged(model, page.orElse(0), "", user.getUsername());
    }

    @PostMapping("/paged_list/name")
    public String getListStudentsPagedByName(HttpSession session, Model model, @ModelAttribute("student_filter") StudentFilter studentFilter) {
        InternalUser user = checkSession(session, SKP_ROLE);
        if (user == null) {
            model.addAttribute("user", new InternalUser());
            return "login";
        }

        model.addAttribute("student_filter", studentFilter);
        return getStudentsPaged(model, 0, studentFilter.getName(), user.getUsername());
    }

    @GetMapping("/paged_list/name")
    public String postListStudentsPagedByName(HttpSession session, Model model, @RequestParam("name") Optional<String> name,
                                          @RequestParam("page") Optional<Integer> page) {
        InternalUser user = checkSession(session, SKP_ROLE);
        if (user == null) {
            model.addAttribute("user", new InternalUser());
            return "login";
        }

        StudentFilter studentFilter = new StudentFilter();
        studentFilter.setName(name.orElse(""));
        model.addAttribute("student_filter", studentFilter);
        return getStudentsPaged(model, page.orElse(0), studentFilter.getName(), user.getUsername());
    }

    @NotNull
    private String getStudentsPaged(Model model, int page, String name, String username) {
        List<Student> students;
        model.addAttribute("name", name);
        if (name.equals("")) {
            students = studentDao.getStudents();
        } else {
            students = studentDao.getStudentsByName(name);
        }
        students.removeIf(student -> student.getUsername().equals(username));
        Collections.sort(students);

        List<List<Student>> studentsPaged = new ArrayList<>();
        int start = 0;
        int pageLength = 8;
        int end = pageLength;
        while (end < students.size()) {
            studentsPaged.add(new ArrayList<>(students.subList(start, end)));
            start += pageLength;
            end += pageLength;
        }
        studentsPaged.add(new ArrayList<>(students.subList(start, students.size())));
        model.addAttribute("students_paged", studentsPaged);

        int totalPages = studentsPaged.size();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("page_numbers", pageNumbers);
        }

        model.addAttribute("selected_page", page);
        return "student/paged_list";
    }

/*    @RequestMapping("/list")
    public String listStudents(HttpSession session, Model model) {
        InternalUser user = checkSession(session, SKP_ROLE);
        if (user == null) {
            model.addAttribute("user", new InternalUser());
            return "login";
        }

        model.addAttribute("students", studentDao.getStudents());
        return "student/list";
    }
*/
    @RequestMapping("/profile")
    public String studentProfile(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        model.addAttribute("student", studentDao.getStudent(user.getUsername()));
        return "student/profile";
    }

    @RequestMapping("/statistics/{username}")
    public String studentStatistics(HttpSession session, Model model, @PathVariable String username) {
        InternalUser user = checkSession(session, SKP_ROLE);
        if (user == null) {
            model.addAttribute("user", new InternalUser());
            return "login";
        }

        Student student = studentDao.getStudent(username);

        Statistics stats = new Statistics();
        List<Collaboration> collaborations = collaborationDao.getCollaborationsStudent(username);
        List<Skill> skills = skillDao.getSkillsOfUsernames(username);
        List<String> skillString = new ArrayList<>();
        for (Skill skill : skills)
            if (!skillString.contains(skill.getName()))
                skillString.add(skill.getName());

        stats.setBalanceHours(student.getBalanceHours());
        stats.setAvgAssesmentScore(Math.round(collaborations.stream().mapToInt(Collaboration::getAssessment).average().orElse(0) * 100.00) / 100.00);
        stats.setTotalHours(collaborations.stream().mapToDouble(Collaboration::getHours).sum());
        stats.setAvgCollaborationHours(Math.round(collaborations.stream().mapToDouble(Collaboration::getHours).average().orElse(0) * 100.00) / 100.00);
        stats.setTotalOffers(offerDao.getOffersStudent(username).size());
        stats.setTotalRequests(requestDao.getRequestsStudent(username).size());
        stats.setTotalCollaborations(collaborations.size());
        stats.setSkillsTakenPart(skillString);

        model.addAttribute("stats", stats);
        model.addAttribute("student", username);
        return "student/statistics";
    }

    @RequestMapping(value = "/add")
    public String addStudent(Model model) {
        Student student = new Student();
        model.addAttribute("student", student);
        return "student/add";
    }

    @PostMapping(value = "/add")
    public String processAddStudent(@ModelAttribute("student") Student student, BindingResult bindingResult) {

        if (!studentDao.getStudentsByUsername(student.getUsername()).isEmpty())
            bindingResult.rejectValue("username", "duplicated", "This username already exists. Please log in.");

        validator.validate(student, bindingResult);
        if (bindingResult.hasErrors()) return "student/add";

        try {
            student.setPassword(encryptor.encryptPassword(student.getPassword()));
            studentDao.addStudent(student);
        } catch (DuplicateKeyException e) {
            throw new SkillSharingException("It already exist the username\n" + e.getMessage(),
                    "PKDuplicate", "student/add");
        } catch (DataAccessException e) {
            throw new SkillSharingException("Error accessing the database\n" + e.getMessage(),
                    "ErrorAccessingDatabase", "/");
        }

        return "redirect:/student/profile";
    }

    @GetMapping(value = "/update/{username}")
    public String updateStudent(Model model, @PathVariable String username) {
        model.addAttribute("student", studentDao.getStudent(username));
        return "student/update";
    }

    @PostMapping(value = "/update")
    public String processUpdateSubmit(@ModelAttribute("student") Student student,
                                      BindingResult bindingResult) {
        updateValidator.validate(student, bindingResult);
        if (bindingResult.hasErrors()) return "student/update";

        student.setPassword(encryptor.encryptPassword(student.getPassword()));
        studentDao.updateStudent(student);
        return "redirect:/student/profile";
    }

    @RequestMapping(value = "/block/{username}")
    public String processBlockStudent(HttpSession session, Model model, @PathVariable String username) {
        InternalUser user = checkSession(session, SKP_ROLE);
        if (user == null) {
            model.addAttribute("user", new InternalUser());
            return "login";
        }

        Student student = studentDao.getStudent(username);
        student.setBlocked(true);
        studentDao.updateStudent(student);

        Email email = new Email();
        email.setReceiver(student.getEmail());
        email.setSubject("Block account");
        email.setBody("Your account has been blocked by the administrator");
        email.setSendDate(LocalDate.now());
        email.setSender("skill.sharing@gmail.com");
        emailDao.addEmail(email);

        return "redirect:../paged_list/";
    }

    @RequestMapping(value = "/unblock/{username}")
    public String processUnblockStudent(HttpSession session, Model model, @PathVariable String username) {
        InternalUser user = checkSession(session, SKP_ROLE);
        if (user == null) {
            model.addAttribute("user", new InternalUser());
            return "login";
        }

        Student student = studentDao.getStudent(username);
        student.setBlocked(false);
        studentDao.updateStudent(student);

        Email email = new Email();
        email.setReceiver(student.getEmail());
        email.setSubject("Block account");
        email.setBody("Your account has been blocked by the administrator");
        email.setSendDate(LocalDate.now());
        email.setSender("skill.sharing@gmail.com");
        emailDao.addEmail(email);

        return "redirect:../paged_list/";
    }
}
