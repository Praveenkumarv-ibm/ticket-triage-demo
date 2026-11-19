package com.example.tickettriagedemo;

import com.example.tickettriagedemo.service.TicketTriageService;
import com.example.tickettriagedemo.service.TicketTriageService.TriageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TicketTriageServiceTest {

    private TicketTriageService triageService;

    @BeforeEach
    void setUp() {
        triageService = new TicketTriageService();
    }

    @ParameterizedTest
    @CsvSource({
            "'Production down', 'All users experiencing outage', 'CRITICAL'",
            "'Security alert', 'Potential security breach in auth module', 'CRITICAL'",
            "'Database problem', 'Severe data loss risk in storage', 'CRITICAL'"
    })
    @DisplayName("Evaluate Critical priority for outage/breach keywords")
    void shouldAssignCriticalPriority(String title, String description, String expectedPriority) {
        TriageResult result = triageService.evaluatePriority(title, description, "AUTO");
        assertEquals(expectedPriority, result.priority());
        assertTrue(result.autoDetected());
        assertNotNull(result.matchedKeyword());
    }

    @ParameterizedTest
    @CsvSource({
            "'Server is down', 'Service unavailable after deployment', 'HIGH'",
            "'Urgent issue', 'Users are blocked from checkout', 'HIGH'",
            "'App crash', 'Mobile client crashes on startup', 'HIGH'"
    })
    @DisplayName("Evaluate High priority for urgent/down/blocked keywords")
    void shouldAssignHighPriority(String title, String description, String expectedPriority) {
        TriageResult result = triageService.evaluatePriority(title, description, "AUTO");
        assertEquals(expectedPriority, result.priority());
        assertTrue(result.autoDetected());
        assertNotNull(result.matchedKeyword());
    }

    @ParameterizedTest
    @CsvSource({
            "'Form validation issue', 'Payment gateway timeout warning', 'MEDIUM'",
            "'Bug in export', 'CSV export has minor formatting glitch', 'MEDIUM'",
            "'Slow dashboard', 'Loading dashboard takes 5 seconds', 'MEDIUM'"
    })
    @DisplayName("Evaluate Medium priority for error/bug/slow keywords")
    void shouldAssignMediumPriority(String title, String description, String expectedPriority) {
        TriageResult result = triageService.evaluatePriority(title, description, "AUTO");
        assertEquals(expectedPriority, result.priority());
        assertTrue(result.autoDetected());
        assertNotNull(result.matchedKeyword());
    }

    @ParameterizedTest
    @CsvSource({
            "'General Question', 'Where can I find API documentation?', 'LOW'",
            "'User Feedback', 'Great tool, here is an inquiry about features', 'LOW'",
            "'Documentation typo', 'Found a cosmetic typo on settings page', 'LOW'"
    })
    @DisplayName("Evaluate Low priority for inquiry/question/docs keywords")
    void shouldAssignLowPriority(String title, String description, String expectedPriority) {
        TriageResult result = triageService.evaluatePriority(title, description, "AUTO");
        assertEquals(expectedPriority, result.priority());
        assertTrue(result.autoDetected());
        assertNotNull(result.matchedKeyword());
    }

    @Test
    @DisplayName("Fallback to Medium when no keywords match in title or description")
    void shouldFallbackWhenNoKeywordsMatch() {
        TriageResult result = triageService.evaluatePriority("Standard Account Update", "Please update the mailing address on record", "AUTO");
        assertEquals("MEDIUM", result.priority());
        assertFalse(result.autoDetected());
    }

    @Test
    @DisplayName("Respect explicit user priority selection over auto-detection")
    void shouldRespectExplicitPriority() {
        TriageResult result = triageService.evaluatePriority("Server down and critical outage", "Huge issue", "LOW");
        assertEquals("LOW", result.priority());
        assertFalse(result.autoDetected());
    }
}
