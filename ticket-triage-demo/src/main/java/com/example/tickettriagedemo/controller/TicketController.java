package com.example.tickettriagedemo.controller;

import com.example.tickettriagedemo.model.Ticket;
import com.example.tickettriagedemo.service.TicketService;
import com.example.tickettriagedemo.service.TicketTriageService.TriageResult;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/")
    public String showPortal(Model model) {
        if (!model.containsAttribute("ticket")) {
            model.addAttribute("ticket", new Ticket());
        }
        model.addAttribute("tickets", ticketService.getAllTickets());
        return "index";
    }

    @PostMapping("/tickets")
    public String submitTicket(@Valid @ModelAttribute("ticket") Ticket ticket,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tickets", ticketService.getAllTickets());
            return "index";
        }

        boolean requestedAuto = (ticket.getPriority() == null || "AUTO".equalsIgnoreCase(ticket.getPriority().trim()));
        TriageResult triageResult = ticketService.processAndSaveTicket(ticket);

        if (requestedAuto) {
            if (triageResult.autoDetected()) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Ticket submitted successfully! Auto-assigned Priority: " + triageResult.priority()
                                + " (matched keyword: '" + triageResult.matchedKeyword() + "').");
            } else {
                redirectAttributes.addFlashAttribute("warningMessage",
                        "Ticket submitted successfully! Priority could not be automatically determined from content and was set to default (" + triageResult.priority() + ").");
            }
        } else {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Ticket submitted successfully with Priority: " + triageResult.priority() + "!");
        }

        return "redirect:/";
    }
}
