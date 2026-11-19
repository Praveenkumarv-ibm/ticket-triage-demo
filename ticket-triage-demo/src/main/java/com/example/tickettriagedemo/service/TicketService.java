package com.example.tickettriagedemo.service;

import com.example.tickettriagedemo.model.Ticket;
import com.example.tickettriagedemo.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
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
        if (ticket.getPriority() == null || ticket.getPriority().isBlank()) {
            ticket.setPriority("MEDIUM");
        }
        if (ticket.getStatus() == null || ticket.getStatus().isBlank()) {
            ticket.setStatus("OPEN");
        }
        return ticketRepository.save(ticket);
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
