package pl.coderslab.controllers;


import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.coderslab.entities.User;
import pl.coderslab.repositories.UserRepository;
import pl.coderslab.securityModel.CurrentUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/login")
    public String getIndex() {
        return "index";
    }

    @PostMapping("/login")
    public void postIndex(@AuthenticationPrincipal CurrentUser customUser,
                          HttpServletRequest request, HttpServletResponse response) throws Exception{

        User user = customUser.getUser();
        HttpSession session = request.getSession();
        session.setAttribute("logged", true);
        session.setAttribute("id", user.getId());
        session.setAttribute("admin", user.isAdmin());
        session.setAttribute("userName", user.getFirstName() + " " + user.getLastName());
        session.setMaxInactiveInterval(60 *60 * 1000);

        checkIfPasswordChanged(user, request, response);
        response.sendRedirect(request.getContextPath() + "/app/main");
    }

    private void checkIfPasswordChanged(User user, HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        if (!user.isPasswordChanged()) {
            response.sendRedirect(request.getContextPath() + "/app/user/newPass");
        }
    }

}
