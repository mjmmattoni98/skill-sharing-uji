package com.aams.skillsharing.controller;

import com.aams.skillsharing.dao.MessageDao;
import com.aams.skillsharing.dao.OfferDao;
import com.aams.skillsharing.dao.RequestDao;
import com.aams.skillsharing.model.InternalUser;
import com.aams.skillsharing.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/message")
public class MessageController extends RoleController{
    private MessageDao messageDao;
    private OfferDao offerDao;
    private RequestDao requestDao;

    @Autowired
    public void setMessageDao(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @Autowired
    public void setOfferDao(OfferDao offerDao) {
        this.offerDao = offerDao;
    }

    @Autowired
    public void setRequestDao(RequestDao requestDao) {
        this.requestDao = requestDao;
    }

    @RequestMapping("/list/{idOffer}/{idRequest}")
    public String listMessages(HttpSession session, Model model, @PathVariable int idOffer, @PathVariable int idRequest) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        List<Message> messages = messageDao.getMessages(idOffer, idRequest);

        model.addAttribute("messages", messages);
        return "message/list";
    }

    @RequestMapping(value = "/add/{idOffer}/{idRequest}")
    public String addMessage(HttpSession session, Model model, @PathVariable int idOffer, @PathVariable int idRequest) {
        if (session.getAttribute("user") == null){
            model.addAttribute("user", new InternalUser());
            return "login";
        }
        InternalUser user = (InternalUser) session.getAttribute("user");

        if (!offerDao.getOffersStudent(user.getUsername()).contains(offerDao.getOffer(idOffer))
                && !requestDao.getRequestsStudent(user.getUsername()).contains(requestDao.getRequest(idRequest)))
            throw new SkillSharingException("You are not allowed to add a message to this collaboration", "NotAllowed", "/");

        Message message = new Message();
        message.setIdOffer(idOffer);
        message.setIdRequest(idRequest);
        model.addAttribute("message", message);
        return "message/add";
    }

    @PostMapping(value = "/add")
    public String processAddMessage(@ModelAttribute("message") Message message) {
        try {
            message.setDateTime(LocalDateTime.now());
            messageDao.addMessage(message);
        }
        catch (DataAccessException e){
            throw new SkillSharingException("Error accessing the database\n" + e.getMessage(),
                    "ErrorAccessingDatabase", "/");
        }
        return "redirect:list/";
    }
}
