package com.example.tickettriagedemo;

import com.example.tickettriagedemo.model.Ticket;
import com.example.tickettriagedemo.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TicketRepository ticketRepository;

    @BeforeEach
    void setUp() {
        ticketRepository.deleteAll();
    }

    @Test
    @DisplayName("UI Rendering: GET / renders portal page with form and empty state")
    void getPortal_ShouldRenderFormAndEmptyTable() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("ticket"))
                .andExpect(model().attributeExists("tickets"))
                .andExpect(model().attribute("tickets", hasSize(0)))
                .andExpect(content().string(containsString("Customer Ticket Portal")))
                .andExpect(content().string(containsString("Submit a New Support Ticket")))
                .andExpect(content().string(containsString("id=\"title\"")))
                .andExpect(content().string(containsString("id=\"customerEmail\"")))
                .andExpect(content().string(containsString("id=\"priority\"")))
                .andExpect(content().string(containsString("id=\"description\"")))
                .andExpect(content().string(containsString("No tickets submitted yet")));
    }

    @Test
    @DisplayName("UI Rendering: GET / renders populated tickets table with badges and email links")
    void getPortal_ShouldRenderPopulatedTicketsTable() throws Exception {
        Ticket ticket = new Ticket("Critical Outage", "Production service is down and returning 500s", "ops@example.com", "CRITICAL");
        ticketRepository.save(ticket);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("tickets", hasSize(1)))
                .andExpect(content().string(containsString("Critical Outage")))
                .andExpect(content().string(containsString("Production service is down and returning 500s")))
                .andExpect(content().string(containsString("mailto:ops@example.com")))
                .andExpect(content().string(containsString("CRITICAL")))
                .andExpect(content().string(containsString("OPEN")))
                .andExpect(content().string(containsString("1 Total")));
    }

    @Test
    @DisplayName("POST /tickets with valid data saves ticket and redirects with flash message")
    void postTicket_ValidData_ShouldSaveAndRedirect() throws Exception {
        mockMvc.perform(post("/tickets")
                        .param("title", "Billing Invoice Issue")
                        .param("description", "Invoice for November is missing tax details")
                        .param("customerEmail", "billing@client.com")
                        .param("priority", "HIGH"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("successMessage"))
                .andExpect(flash().attribute("successMessage", "Ticket submitted successfully!"));

        assertEquals(1, ticketRepository.count());
        Ticket saved = ticketRepository.findAll().get(0);
        assertEquals("Billing Invoice Issue", saved.getTitle());
        assertEquals("Invoice for November is missing tax details", saved.getDescription());
        assertEquals("billing@client.com", saved.getCustomerEmail());
        assertEquals("HIGH", saved.getPriority());
        assertEquals("OPEN", saved.getStatus());
    }

    @Test
    @DisplayName("UI Rendering: POST /tickets with validation errors renders error feedback and is-invalid classes")
    void postTicket_InvalidData_ShouldRenderValidationErrors() throws Exception {
        mockMvc.perform(post("/tickets")
                        .param("title", "AB") // too short
                        .param("description", "short") // too short
                        .param("customerEmail", "invalid-email") // invalid format
                        .param("priority", "MEDIUM"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("ticket", "title", "description", "customerEmail"))
                .andExpect(content().string(containsString("is-invalid")))
                .andExpect(content().string(containsString("Title must be between 3 and 150 characters")))
                .andExpect(content().string(containsString("Description must be between 10 and 2000 characters")))
                .andExpect(content().string(containsString("Please provide a valid email address")));

        assertEquals(0, ticketRepository.count());
    }

    @Test
    @DisplayName("UI Rendering: Flash message renders on redirected GET /")
    void flashMessage_ShouldRenderOnRedirect() throws Exception {
        mockMvc.perform(get("/").flashAttr("successMessage", "Ticket submitted successfully!"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ticket submitted successfully!")))
                .andExpect(content().string(containsString("alert alert-success")));
    }
}
