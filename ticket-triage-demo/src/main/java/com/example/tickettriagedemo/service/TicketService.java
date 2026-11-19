package com.example.tickettriagedemo.service;

import com.example.tickettriagedemo.model.Ticket;
import com.example.tickettriagedemo.repository.TicketRepository;
import com.example.tickettriagedemo.service.TicketTriageService.TriageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketTriageService ticketTriageService;

    public TicketService(TicketRepository ticketRepository, TicketTriageService ticketTriageService) {
        this.ticketRepository = ticketRepository;
        this.ticketTriageService = ticketTriageService;
    }

    @Transactional(readOnly = true)
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Optional<Ticket> getTicketById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return ticketRepository.findById(id);
    }

    public Ticket saveTicket(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket cannot be null");
        }
        TriageResult triageResult = ticketTriageService.evaluatePriority(
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority()
        );
        ticket.setPriority(triageResult.priority());

        if (ticket.getStatus() == null || ticket.getStatus().isBlank()) {
            ticket.setStatus("OPEN");
        }
        return ticketRepository.save(ticket);
    }

    public TriageResult processAndSaveTicket(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket cannot be null");
        }
        TriageResult triageResult = ticketTriageService.evaluatePriority(
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority()
        );
        ticket.setPriority(triageResult.priority());

        if (ticket.getStatus() == null || ticket.getStatus().isBlank()) {
            ticket.setStatus("OPEN");
        }
        ticketRepository.save(ticket);
        return triageResult;
    }

    public Ticket updateTicketStatus(Long id, String newStatus) {
        if (id == null) {
            throw new IllegalArgumentException("Ticket ID cannot be null");
        }
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("New status cannot be null or empty");
        }
        String normalizedStatus = newStatus.toUpperCase().trim();
        if (!normalizedStatus.matches("OPEN|IN_PROGRESS|RESOLVED|CLOSED")) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with id: " + id));
        ticket.setStatus(normalizedStatus);
        return ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public long getTicketCount() {
        return ticketRepository.count();
    }
}
