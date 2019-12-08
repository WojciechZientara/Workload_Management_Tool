package pl.coderslab.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import pl.coderslab.entities.BauReport;
import pl.coderslab.entities.Client;
import pl.coderslab.entities.User;
import pl.coderslab.repositories.BauReportRepository;
import pl.coderslab.repositories.ClientRepository;
import pl.coderslab.repositories.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
public class ReportsController {

    @Autowired
    BauReportRepository bauReportRepository;

    @Autowired
    ClientRepository clientRepository;
    
    @GetMapping("/app/reports")
    public String getReports(Model model) {
        List<BauReport> bauReports = bauReportRepository.findAll();
        model.addAttribute("bauReports", bauReports);
        return "app/reports";
    }

    @GetMapping("/app/reports/add")
    public String getAddReport(Model model) {
        model.addAttribute("bauReport", new BauReport());
        model.addAttribute("clients", clientRepository.findAll());
        return "app/saveReport";
    }

    @PostMapping("/app/reports/add")
    public String postAddReport(@Valid BauReport bauReport, BindingResult result, Model model,
                            HttpServletRequest request, HttpServletResponse response ) throws IOException {

        try{
            if (result.hasErrors()) {
                return "app/saveReport";
            } else {
                bauReportRepository.save(bauReport);
                response.sendRedirect(request.getContextPath() + "/app/reports");
            }
        } catch (Exception e) {
            model.addAttribute("reportExists", true);
            return "app/saveReport";
        }
        return null;
    }

    @GetMapping("/app/reports/edit/{reportId}")
    public String getEditReport(@PathVariable long reportId, Model model) {
        BauReport bauReport = bauReportRepository.findOne(reportId);
        model.addAttribute("bauReport", bauReport);
        model.addAttribute("clients", clientRepository.findAll());
        model.addAttribute("edit", true);
        return "app/saveReport";
    }

    @PostMapping("/app/reports/edit/{reportId}")
    public String postIndex(@PathVariable long reportId, @Valid BauReport bauReport, BindingResult result, Model model,
                            HttpServletRequest request, HttpServletResponse response ) throws IOException {

        try{
            if (result.hasErrors()) {
                return "app/saveReport";
            } else {
                BauReport reportToUpdate = bauReportRepository.findOne(reportId);
                reportToUpdate.setName(bauReport.getName());
                reportToUpdate.setClient(bauReport.getClient());
                bauReportRepository.save(reportToUpdate);
                response.sendRedirect(request.getContextPath() + "/app/reports");
            }
        } catch (Exception e) {
            return "app/saveReport";
        }
        return null;
    }

    @GetMapping("/app/reports/delete/{reportId}")
    public void postIndex(@PathVariable long reportId,
                            HttpServletRequest request, HttpServletResponse response ) throws IOException {
        bauReportRepository.delete(reportId);
        response.sendRedirect(request.getContextPath() + "/app/reports");
    }

}
