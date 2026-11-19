package com.example.tickettriagedemo;

import com.example.tickettriagedemo.model.Ticket;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TicketValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Valid ticket should have zero constraint violations")
    void validTicket_ShouldHaveNoViolations() {
        Ticket ticket = new Ticket("Payment Issue", "Cannot checkout using credit card on portal", "customer@example.com", "HIGH");
        Set<ConstraintViolation<Ticket>> violations = validator.validate(ticket);
        assertTrue(violations.isEmpty(), "Expected no violations for valid ticket");
    }

    @ParameterizedTest
    @ValueSource(strings = {"AUTO", "LOW", "MEDIUM", "HIGH", "CRITICAL"})
    @DisplayName("Allowed priority values including AUTO should pass validation")
    void allowedPriorities_ShouldPassValidation(String priority) {
        Ticket ticket = new Ticket("Valid Title", "Valid description with sufficient length", "user@example.com", priority);
        Set<ConstraintViolation<Ticket>> violations = validator.validate(ticket);
        assertTrue(violations.isEmpty(), "Expected priority " + priority + " to pass validation");
    }

    @Test
    @DisplayName("Blank or short title (< 3 chars) should produce validation violations")
    void invalidTitle_ShouldProduceViolations() {
        Ticket blankTitle = new Ticket("", "Detailed description with enough length", "test@example.com");
        Set<ConstraintViolation<Ticket>> blankViolations = validator.validate(blankTitle);
        assertFalse(blankViolations.isEmpty());
        assertTrue(blankViolations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));

        Ticket shortTitle = new Ticket("AB", "Detailed description with enough length", "test@example.com");
        Set<ConstraintViolation<Ticket>> shortViolations = validator.validate(shortTitle);
        assertFalse(shortViolations.isEmpty());
        assertTrue(shortViolations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));

        Ticket nullTitle = new Ticket(null, "Detailed description with enough length", "test@example.com");
        Set<ConstraintViolation<Ticket>> nullViolations = validator.validate(nullTitle);
        assertFalse(nullViolations.isEmpty());
        assertTrue(nullViolations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    @DisplayName("Description too short (< 10 chars) or blank should produce violations")
    void invalidDescription_ShouldProduceViolations() {
        Ticket shortDesc = new Ticket("Valid Title", "Short", "test@example.com");
        Set<ConstraintViolation<Ticket>> violations = validator.validate(shortDesc);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));

        Ticket blankDesc = new Ticket("Valid Title", "   ", "test@example.com");
        Set<ConstraintViolation<Ticket>> blankViolations = validator.validate(blankDesc);
        assertFalse(blankViolations.isEmpty());
        assertTrue(blankViolations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "user@", "@domain.com", "user@domain", "plainaddress"})
    @DisplayName("Malformed customer email addresses should be rejected by validation regex")
    void malformedEmails_ShouldProduceViolations(String malformedEmail) {
        Ticket ticket = new Ticket("Valid Title", "Valid description with sufficient length", malformedEmail);
        Set<ConstraintViolation<Ticket>> violations = validator.validate(ticket);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("customerEmail")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"user@example.com", "first.last@company.org", "customer+support@domain.co.uk", "a.b-c@domain.io"})
    @DisplayName("Well-formed customer email addresses should pass validation")
    void wellFormedEmails_ShouldPassValidation(String validEmail) {
        Ticket ticket = new Ticket("Valid Title", "Valid description with sufficient length", validEmail);
        Set<ConstraintViolation<Ticket>> violations = validator.validate(ticket);
        assertTrue(violations.isEmpty(), "Expected valid email " + validEmail + " to pass validation");
    }

    @Test
    @DisplayName("Invalid priority value should produce constraint violation")
    void invalidPriority_ShouldProduceViolations() {
        Ticket ticket = new Ticket("Valid Title", "Valid description with sufficient length", "user@example.com");
        ticket.setPriority("SUPER_CRITICAL");
        Set<ConstraintViolation<Ticket>> violations = validator.validate(ticket);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("priority")));
    }

    @Test
    @DisplayName("Invalid status value should produce constraint violation")
    void invalidStatus_ShouldProduceViolations() {
        Ticket ticket = new Ticket("Valid Title", "Valid description with sufficient length", "user@example.com");
        ticket.setStatus("ARCHIVED");
        Set<ConstraintViolation<Ticket>> violations = validator.validate(ticket);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("status")));
    }
}
