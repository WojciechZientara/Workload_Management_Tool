package pl.coderslab.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import pl.coderslab.entities.BauReport;
import pl.coderslab.repositories.BauReportRepository;
import pl.coderslab.repositories.ClientRepository;
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
    
    @GetMapping("/admin/reports")
    public String getDisplayReports(Model model) {
        List<BauReport> bauReports = bauReportRepository.findAll();
        model.addAttribute("bauReports", bauReports);
        return "admin/displayReports";
    }

    @GetMapping("/admin/addReport")
    public String getAddReport(Model model) {
        model.addAttribute("bauReport", new BauReport());
        model.addAttribute("clients", clientRepository.findAll());
        return "admin/saveReport";
    }

    @PostMapping("/admin/addReport")
    public String postAddReport(@Valid BauReport bauReport, BindingResult result, Model model,
                            HttpServletRequest request, HttpServletResponse response ) throws IOException {

        try{
            if (result.hasErrors()) {
                return "admin/saveReport";
            } else {
                bauReport.setAverageDuration(bauReport.getAverageDuration() * 60);
                bauReportRepository.save(bauReport);
                response.sendRedirect(request.getContextPath() + "/admin/reports");
            }
        } catch (Exception e) {
            model.addAttribute("reportExists", true);
            return "admin/saveReport";
        }
        return null;
    }

    @GetMapping("/admin/editReport/{reportId}")
    public String getEditReport(@PathVariable long reportId, Model model) {
        BauReport bauReport = bauReportRepository.findOne(reportId);
        bauReport.setAverageDuration(bauReport.getAverageDuration() / 60);
        model.addAttribute("bauReport", bauReport);
        model.addAttribute("clients", clientRepository.findAll());
        model.addAttribute("edit", true);
        return "admin/saveReport";
    }

    @PostMapping("/admin/editReport/{reportId}")
    public String postIndex(@PathVariable long reportId, @Valid BauReport bauReport, BindingResult result, Model model,
                            HttpServletRequest request, HttpServletResponse response ) throws IOException {

        try{
            if (result.hasErrors()) {
                return "admin/saveReport";
            } else {
                BauReport reportToUpdate = bauReportRepository.findOne(reportId);
                reportToUpdate.setName(bauReport.getName());
                reportToUpdate.setClient(bauReport.getClient());
                reportToUpdate.setAverageDuration(bauReport.getAverageDuration() * 60);
                bauReportRepository.save(reportToUpdate);
                response.sendRedirect(request.getContextPath() + "/admin/reports");
            }
        } catch (Exception e) {
            return "admin/saveReport";
        }
        return null;
    }

    @GetMapping("/admin/deleteReport/{reportId}")
    public void postIndex(@PathVariable long reportId,
                            HttpServletRequest request, HttpServletResponse response ) throws IOException {
        bauReportRepository.delete(reportId);
        response.sendRedirect(request.getContextPath() + "/admin/reports");
    }

}
