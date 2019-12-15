package pl.coderslab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import pl.coderslab.entities.Role;
import pl.coderslab.entities.User;
import pl.coderslab.repositories.UserRepository;
import pl.coderslab.securityModel.CurrentUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    @PostMapping("/login")
    public void postLogin(@AuthenticationPrincipal CurrentUser customUser,
                          HttpServletRequest request, HttpServletResponse response) throws Exception{

        User user = customUser.getUser();
        HttpSession session = request.getSession();
        session.setAttribute("logged", true);
        session.setAttribute("id", user.getId());
        session.setAttribute("admin", false);
        for (Role role : user.getRoles()) {
            if (role.getName().equals("ROLE_ADMIN")) {
                session.setAttribute("admin", true);
            }
        }
        session.setAttribute("userName", user.getFirstName() + " " + user.getLastName());
        session.setMaxInactiveInterval(60 *60 * 1000);

        if (!user.isPasswordChanged()) {
            response.sendRedirect(request.getContextPath() + "/app/changePassword");
        } else if ((boolean)session.getAttribute("admin")) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/app/userPanel");
        }
    }

}
