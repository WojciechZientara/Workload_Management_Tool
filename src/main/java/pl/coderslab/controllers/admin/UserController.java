package pl.coderslab.controllers.admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.entities.Client;
import pl.coderslab.entities.Role;
import pl.coderslab.entities.Task;
import pl.coderslab.entities.User;
import pl.coderslab.repositories.ClientRepository;
import pl.coderslab.repositories.RoleRepository;
import pl.coderslab.repositories.TaskRepository;
import pl.coderslab.repositories.UserRepository;
import pl.coderslab.services.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Controller
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    TaskRepository taskRepository;

    @ModelAttribute("clients")
    public List<Client> getClients() {
        return clientRepository.findAll();
    }

    @ModelAttribute("roles")
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @GetMapping("/admin/users")
    public String getDisplayUsers(Model model) {
        List<User> users = userRepository.findAllWithClients();
        model.addAttribute("users", users);
        return "admin/displayUsers";
    }

    @GetMapping("/admin/addUser")
    public String getAddUser(Model model) {
        model.addAttribute("user", new User());
        return "admin/saveUser";
    }

    @PostMapping("/admin/addUser")
    public String postAddUser(@Valid User user, BindingResult result, Model model,
                            HttpServletRequest request, HttpServletResponse response ) throws IOException {

        try{
            if (result.hasErrors()) {
                return "admin/saveUser";
            } else {
                Set<Role> roles = user.getRoles();
                user.setRoles(null);
                user.setFullName(user.getFirstName() + " " + user.getLastName());
                userService.saveUser(user);
                for (Client client : user.getClients()) {
                    Client usersClient = clientRepository.findClientWithUsers(client.getId());
                    usersClient.getUsers().add(user);
                    clientRepository.save(usersClient);
                }
                for (Role role : roles) {
                    roleRepository.createUserRoleAssociation(user.getId(), role.getId());
                }
                response.sendRedirect(request.getContextPath() + "/admin/users");
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("userExists", true);
            return "admin/saveUser";
        }
        return null;
    }

    @GetMapping("/admin/editUser/{userId}")
    public String getEditUser(@PathVariable long userId, Model model) {
        User user = userRepository.findOneWithClients(userId);
        model.addAttribute("user", user);
        model.addAttribute("edit", true);
        return "admin/saveUser";
    }

    @PostMapping("/admin/editUser/{userId}")
    public String postEditUser(@PathVariable long userId, @Valid User user, BindingResult result, Model model,
                            HttpServletRequest request, HttpServletResponse response ) throws IOException {

        try{
            if (result.hasErrors()) {
                return "admin/saveUser";
            } else {
                userRepository.clearUsersClientAssociations(userId);
                userRepository.clearUserRoleAssociations(userId);

                User userToUpdate = userRepository.findOne(userId);
                userToUpdate.setId(userId);
                userToUpdate.setFirstName(user.getFirstName());
                userToUpdate.setLastName(user.getLastName());
                userToUpdate.setFullName(user.getFirstName() + " " + user.getLastName());
                userToUpdate.setEmail(user.getEmail());
                userToUpdate.setClients(user.getClients());
                userRepository.save(userToUpdate);

//                userToUpdate.setRoles(null);
                for (Role role : user.getRoles()) {
                    roleRepository.createUserRoleAssociation(userToUpdate.getId(), role.getId());
                }

                for (Client client : userToUpdate.getClients()) {
                    Client usersClient = clientRepository.findClientWithUsers(client.getId());
                    Client clientWithBau = clientRepository.findClientWithBauReports(client.getId());
                    usersClient.setBauReportList(clientWithBau.getBauReportList());
                    usersClient.getUsers().add(userToUpdate);
                    clientRepository.save(usersClient);
                }

                response.sendRedirect(request.getContextPath() + "/admin/users");
            }
        } catch (Exception e) {
            model.addAttribute("userExists", true);
            model.addAttribute("edit", true);
            return "admin/saveUser";
        }
        return null;
    }

    @GetMapping("/admin/deleteUser/{userId}")
    public void postIndex(@PathVariable long userId,
                            HttpServletRequest request, HttpServletResponse response ) throws IOException {
        userRepository.clearUserActivitiesAssociations(userId);
        userRepository.clearUserTasksAssociations(userId);
        userRepository.clearUsersClientAssociations(userId);
        userRepository.clearUserRoleAssociations(userId);
        userRepository.delete(userId);
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }

}
