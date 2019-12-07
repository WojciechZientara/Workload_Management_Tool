package pl.coderslab.controllers;

import org.hibernate.Hibernate;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.entities.Client;
import pl.coderslab.entities.User;
import pl.coderslab.repositories.ClientRepository;
import pl.coderslab.repositories.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClientRepository clientRepository;

    @ModelAttribute("clients")
    public List<Client> getClients() {
        return clientRepository.findAll();
    }

    @GetMapping("/app/user/newPass")
    public String getIndex(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        model.addAttribute("user", userRepository.findOne((long) session.getAttribute("id")));
        return "app/newPass";
    }

    @PostMapping("/app/user/newPass")
    public String postIndex(@RequestParam String userId ,@RequestParam String password1, @RequestParam String password2,
                            Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        User user = userRepository.findOne(Long.parseLong(userId));

        try {
            if (password1.equals(password2) && !"".equals(password1)) {
                HttpSession session = request.getSession();
                user.setPassword(password1);
                user.setPasswordChanged(true);
                userRepository.save(user);
                response.sendRedirect(request.getContextPath() + "/app/main");
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            model.addAttribute("incorrectCredentials", true);
            model.addAttribute("user", user);
            return "app/newPass";
        }
        return null;
    }

    @GetMapping("/app/users")
    public String getUsers(Model model) {
        List<User> users = userRepository.findAllWithClients();
        model.addAttribute("users", users);
        return "app/users";
    }

    @GetMapping("/app/users/edit/{userId}")
    public String getEditUser(@PathVariable long userId, Model model) {
        User user = userRepository.findOneWithClients(userId);
        model.addAttribute("user", user);
        model.addAttribute("edit", true);
        return "app/register";
    }

    @PostMapping("/app/users/edit/{userId}")
    public String postIndex(@PathVariable long userId, @Valid User user, BindingResult result, Model model,
                            HttpServletRequest request, HttpServletResponse response ) throws IOException {

        try{
            if (result.hasErrors()) {
                return "app/register";
            } else {
                userRepository.clearUsersClientAssociations(userId);

                User userToUpdate = userRepository.findOne(userId);
                userToUpdate.setId(userId);
                userToUpdate.setFirstName(user.getFirstName());
                userToUpdate.setLastName(user.getLastName());
                userToUpdate.setEmail(user.getEmail());
                userToUpdate.setEmail(user.getEmail());
                userToUpdate.setAdmin(user.isAdmin());
                userToUpdate.setClients(user.getClients());
                userRepository.save(userToUpdate);

                for (Client client : userToUpdate.getClients()) {

                    Client clientToBeModified = clientRepository.findClientWithUsers(client.getId());
                    Client dummyClient = clientRepository.findClientWithBauReports(client.getId());
                    clientToBeModified.setBauReportList(dummyClient.getBauReportList());
                    clientToBeModified.getUsers().add(userToUpdate);
                    clientRepository.save(clientToBeModified);
                }

                response.sendRedirect(request.getContextPath() + "/app/users");
            }
        } catch (Exception e) {
            model.addAttribute("userExists", true);
            model.addAttribute("edit", true);
            return "app/register";
        }
        return null;
    }

    @GetMapping("/app/users/delete/{userId}")
    public void postIndex(@PathVariable long userId,
                            HttpServletRequest request, HttpServletResponse response ) throws IOException {
        userRepository.clearUsersClientAssociations(userId);
        userRepository.delete(userId);
        response.sendRedirect(request.getContextPath() + "/app/users");
    }

}
