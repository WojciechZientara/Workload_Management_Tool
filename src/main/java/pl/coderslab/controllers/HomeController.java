package pl.coderslab.controllers;


import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.coderslab.entities.User;
import pl.coderslab.repositories.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public String getIndex() {
        return "index";
    }

    @PostMapping("/")
    public String postIndex(@RequestParam String email, @RequestParam String password,
                            Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        User user = userRepository.findUserByEmail(email);

        try {
            if (BCrypt.checkpw(password, user.getPassword())) {
                checkIfPasswordChanged(user, request, response);
                HttpSession session = request.getSession();
                session.setAttribute("logged", true);
                session.setAttribute("id", user.getId());
                session.setAttribute("admin", user.isAdmin());
                session.setAttribute("userName", user.getFirstName() + " " + user.getLastName());
                response.sendRedirect(request.getContextPath() + "/app/main");
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            model.addAttribute("incorrectCredentials", true);
            return "index";
        }
        return null;
    }

    private void checkIfPasswordChanged(User user, HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        if (!user.isPasswordChanged()) {
            response.sendRedirect(request.getContextPath() + "/app/user/newPass");
        }
    }

}
