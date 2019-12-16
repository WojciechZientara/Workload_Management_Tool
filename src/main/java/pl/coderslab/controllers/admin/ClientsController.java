package pl.coderslab.controllers.admin;

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
        return userRepository.findAll();
    }
    
    @GetMapping("/admin/clients")
    public String getDisplayClients(Model model) {
        List<Client> clients = clientRepository.findAllWithUsers();
        for (Client client : clients) {
            client.setBauReportList(clientRepository.findClientWithBauReports(client.getId()).getBauReportList());
        }
        model.addAttribute("clients", clients);
        return "admin/displayClients";
    }

    @GetMapping("/admin/addClient")
    public String getAddClient(Model model) {
        model.addAttribute("client", new Client());
        return "admin/saveClient";
    }

    @PostMapping("/admin/addClient")
    public String postAddClient(@Valid Client client, BindingResult result, Model model,
                            HttpServletRequest request, HttpServletResponse response ) throws IOException {

        try{
            if (result.hasErrors()) {
                return "admin/saveClient";
            } else {
                clientRepository.save(client);
                response.sendRedirect(request.getContextPath() + "/admin/clients");
            }
        } catch (Exception e) {
            model.addAttribute("clientExists", true);
            return "admin/saveClient";
        }
        return null;
    }

    @GetMapping("/admin/editClient/{clientId}")
    public String getEditClient(@PathVariable long clientId, Model model) {
        Client client = clientRepository.findClientWithUsers(clientId);
        client.setBauReportList(clientRepository.findClientWithBauReports(client.getId()).getBauReportList());
        model.addAttribute("client", client);
        model.addAttribute("edit", true);
        return "admin/saveClient";
    }

    @PostMapping("/admin/editClient/{clientId}")
    public String postEditClient(@PathVariable long clientId, @Valid Client client, BindingResult result, Model model,
                            HttpServletRequest request, HttpServletResponse response ) throws IOException {

        try{
            if (result.hasErrors()) {
                return "admin/saveClient";
            } else {
                client.setId(clientId);
                clientRepository.save(client);
                response.sendRedirect(request.getContextPath() + "/admin/clients");
            }
        } catch (Exception e) {
            return "admin/saveClient";
        }
        return null;
    }

    @GetMapping("/admin/deleteClient/{clientId}")
    public void getDeleteClient(@PathVariable long clientId,
                            HttpServletRequest request, HttpServletResponse response ) throws IOException {
        clientRepository.clearClientsUsersAssociations(clientId);
        clientRepository.delete(clientId);
        response.sendRedirect(request.getContextPath() + "/admin/clients");
    }

}
