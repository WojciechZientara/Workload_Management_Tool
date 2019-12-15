package pl.coderslab.controllers.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.coderslab.entities.User;
import pl.coderslab.repositories.UserRepository;
import pl.coderslab.services.UserService;
import pl.coderslab.services.UserServiceImpl;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class UserProfileController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @GetMapping("/app/userProfile")
    public String getUserProfile(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        model.addAttribute("user", userRepository.findOne((long) session.getAttribute("id")));
        return "app/displayUserProfile";
    }

    @GetMapping("/app/changePassword")
    public String getChangePassword(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        model.addAttribute("user", userRepository.findOne((long) session.getAttribute("id")));
        return "app/changePassword";
    }

    @PostMapping("/app/changePassword")
    public String postChangePassword(@RequestParam String userId,
                                     @RequestParam String password1, @RequestParam String password2, Model model,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {

        User user = userRepository.findOne(Long.parseLong(userId));

        try {
            if (password1.equals(password2) && !"".equals(password1)) {
                HttpSession session = request.getSession();
                user.setPassword(password1);
                user.setPasswordChanged(true);
                userService.saveUser(user);
                if ((boolean)session.getAttribute("admin")) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                } else {
                    response.sendRedirect(request.getContextPath() + "/app/userPanel");
                }
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            model.addAttribute("incorrectCredentials", true);
            model.addAttribute("user", user);
            return "app/changePassword";
        }
        return null;
    }

}
