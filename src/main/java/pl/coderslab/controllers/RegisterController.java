package pl.coderslab.controllers;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.coderslab.entities.Client;
import pl.coderslab.entities.User;
import pl.coderslab.repositories.ClientRepository;
import pl.coderslab.repositories.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
public class RegisterController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClientRepository clientRepository;

    @ModelAttribute("clients")
    public List<Client> getClients() {
        return clientRepository.findAll();
    }

    @GetMapping("/app/register")
    public String getIndex(Model model) {
        model.addAttribute("user", new User());
        return "app/register";
    }

    @PostMapping("/app/register")
    public String postIndex(@Valid User user, BindingResult result, Model model,
                            HttpServletRequest request, HttpServletResponse response ) throws IOException {

        try{
            if (result.hasErrors()) {
                return "app/register";
            } else {
                user.setPasswordChanged(false);
                user.setDailyWorkingHours(8);
                userRepository.save(user);
                for (Client client : user.getClients()) {
                    Client modifiedClient = clientRepository.findClientWithUsers(client.getId());
                    modifiedClient.getUsers().add(user);
                    clientRepository.save(modifiedClient);
                }
                response.sendRedirect(request.getContextPath() + "/app/users");
            }
        } catch (Exception e) {
            model.addAttribute("userExists", true);
            return "app/register";
        }
        return null;
    }

}
