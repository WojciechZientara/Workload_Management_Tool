package pl.coderslab.controllers;

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
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
public class ClientsController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClientRepository clientRepository;

    @ModelAttribute("users")
    public List<User> getUsers() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.setLastName(user.getFirstName() + " " + user.getLastName());
        }
        return users;
    }
    
    @GetMapping("/app/clients")
    public String getClients(Model model) {
        List<Client> clients = clientRepository.findAllWithUsers();
        for (Client client : clients) {
            client.setBauReportList(clientRepository.findClientWithBauReports(client.getId()).getBauReportList());
        }
        model.addAttribute("clients", clients);
        return "app/clients";
    }

    @GetMapping("/app/clients/add")
    public String getAddClient(Model model) {
        model.addAttribute("client", new Client());
        return "app/saveClient";
    }

    @PostMapping("/app/clients/add")
    public String postAddClient(@Valid Client client, BindingResult result, Model model,
                            HttpServletRequest request, HttpServletResponse response ) throws IOException {

        try{
            if (result.hasErrors()) {
                return "app/saveClient";
            } else {
                clientRepository.save(client);
                response.sendRedirect(request.getContextPath() + "/app/clients");
            }
        } catch (Exception e) {
            model.addAttribute("clientExists", true);
            return "app/saveClient";
        }
        return null;
    }

    @GetMapping("/app/clients/edit/{clientId}")
    public String getEditClient(@PathVariable long clientId, Model model) {
        Client client = clientRepository.findClientWithUsers(clientId);
        client.setBauReportList(clientRepository.findClientWithBauReports(client.getId()).getBauReportList());
        model.addAttribute("client", client);
        model.addAttribute("edit", true);
        return "app/saveClient";
    }

    @PostMapping("/app/clients/edit/{clientId}")
    public String postIndex(@PathVariable long clientId, @Valid Client client, BindingResult result, Model model,
                            HttpServletRequest request, HttpServletResponse response ) throws IOException {

        try{
            if (result.hasErrors()) {
                return "app/saveClient";
            } else {
                client.setId(clientId);
                clientRepository.save(client);
                response.sendRedirect(request.getContextPath() + "/app/clients");
            }
        } catch (Exception e) {
            return "app/register";
        }
        return null;
    }

    @GetMapping("/app/clients/delete/{clientId}")
    public void postIndex(@PathVariable long clientId,
                            HttpServletRequest request, HttpServletResponse response ) throws IOException {
        clientRepository.clearClientsUsersAssociations(clientId);
        clientRepository.delete(clientId);
        response.sendRedirect(request.getContextPath() + "/app/clients");
    }

}
