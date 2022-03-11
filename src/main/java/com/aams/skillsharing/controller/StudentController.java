package com.aams.skillsharing.controller;

import com.aams.skillsharing.dao.StudentDao;
import com.aams.skillsharing.model.InternalUser;
import com.aams.skillsharing.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/student")
public class StudentController extends RoleController {
    private StudentDao studentDao;
    private static final StudentValidator validator = new StudentValidator();

    @Autowired
    public void setStudentDao(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    @RequestMapping("/profile")
    public String studentProfile(HttpSession session, Model model){
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        model.addAttribute("student", studentDao.getStudent(user.getUsername()));
        return "student/profile";
    }

    @RequestMapping(value = "/add")
    public String addStudent(Model model) {
        Student student = new Student();
        model.addAttribute("student", student);
        return "student/add";
    }

    @PostMapping(value = "/add")
    public String processAddStudent(@ModelAttribute("student") Student student, BindingResult bindingResult) {

        validator.validate(student, bindingResult);
        if (bindingResult.hasErrors()) return "student/add";

        try{
            studentDao.addStudent(student);
        }
        catch (DuplicateKeyException e){
            throw new SkillSharingException("It already exist the username\n" + e.getMessage(),
                    "PKDuplicate", "student/add");
        }
        catch (DataAccessException e){
            throw new SkillSharingException("Error accessing the database\n" + e.getMessage(),
                    "ErrorAccessingDatabase", "/");
        }
        return "redirect:/student/profile";
    }

    @GetMapping(value = "/update/{username}")
    public String updateStudent(HttpSession session, Model model, @PathVariable String username) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        if (!user.getUsername().equals(username)){
            throw new SkillSharingException("You can't update other users info", "NotYourUser", "/");
        }
        model.addAttribute("student", studentDao.getStudent(username));
        return "student/update";
    }

    @PostMapping(value = "/update")
    public String processUpdateSubmit(@ModelAttribute("student") Student student,
                                      BindingResult bindingResult) {
        validator.validate(student, bindingResult);
        if (bindingResult.hasErrors()) return "student/update";
        studentDao.updateStudent(student);
        return "redirect:/student/perfil";
    }
}
