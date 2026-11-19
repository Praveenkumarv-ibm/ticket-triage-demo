package com.example.tickettriagedemo;

import com.example.tickettriagedemo.model.Ticket;
import com.example.tickettriagedemo.repository.TicketRepository;
import com.example.tickettriagedemo.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    private Ticket sampleTicket;

    @BeforeEach
    void setUp() {
        sampleTicket = new Ticket("Network Latency", "Network requests are intermittently timing out", "admin@domain.com", "HIGH");
        sampleTicket.setId(10L);
    }

    @Test
    @DisplayName("getAllTickets should delegate to repository findAllByOrderByCreatedAtDesc")
    void getAllTickets_ShouldReturnOrderedList() {
        when(ticketRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(sampleTicket));

        List<Ticket> result = ticketService.getAllTickets();

        assertEquals(1, result.size());
        assertEquals("Network Latency", result.get(0).getTitle());
        verify(ticketRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("getTicketById should return Optional of Ticket")
    void getTicketById_ShouldReturnTicketWhenExists() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));

        Optional<Ticket> result = ticketService.getTicketById(10L);

        assertTrue(result.isPresent());
        assertEquals("admin@domain.com", result.get().getCustomerEmail());
    }

    @Test
    @DisplayName("getTicketById with null id should return empty Optional")
    void getTicketById_NullIdShouldReturnEmpty() {
        Optional<Ticket> result = ticketService.getTicketById(null);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("saveTicket should apply defaults and save ticket")
    void saveTicket_ShouldApplyDefaultsAndSave() {
        Ticket rawTicket = new Ticket("Printer Out", "Printer on 2nd floor is out of paper", "user@company.com");
        rawTicket.setPriority(null);
        rawTicket.setStatus(null);

        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ticket saved = ticketService.saveTicket(rawTicket);

        assertNotNull(saved);
        assertEquals("MEDIUM", saved.getPriority());
        assertEquals("OPEN", saved.getStatus());
        verify(ticketRepository).save(rawTicket);
    }

    @Test
    @DisplayName("saveTicket with null should throw IllegalArgumentException")
    void saveTicket_NullShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> ticketService.saveTicket(null));
    }

    @Test
    @DisplayName("updateTicketStatus should update valid status")
    void updateTicketStatus_ShouldUpdateStatus() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(sampleTicket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ticket updated = ticketService.updateTicketStatus(10L, "RESOLVED");

        assertEquals("RESOLVED", updated.getStatus());
        verify(ticketRepository).save(sampleTicket);
    }

    @Test
    @DisplayName("updateTicketStatus with invalid status should throw IllegalArgumentException")
    void updateTicketStatus_InvalidStatusShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> ticketService.updateTicketStatus(10L, "INVALID_STATUS"));
    }

    @Test
    @DisplayName("updateTicketStatus for non-existent ticket should throw IllegalArgumentException")
    void updateTicketStatus_NonExistentTicketShouldThrow() {
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> ticketService.updateTicketStatus(999L, "CLOSED"));
    }
}
