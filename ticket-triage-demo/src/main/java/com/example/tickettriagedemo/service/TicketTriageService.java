package com.example.tickettriagedemo.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TicketTriageService {

    public record TriageResult(String priority, boolean autoDetected, String matchedKeyword) {
    }

    private static final List<String> CRITICAL_KEYWORDS = List.of(
            "critical", "outage", "security breach", "data loss", "production down"
    );

    private static final List<String> HIGH_KEYWORDS = List.of(
            "down", "urgent", "emergency", "crash", "unavailable", "blocked", "severe", "fatal"
    );

    private static final List<String> MEDIUM_KEYWORDS = List.of(
            "error", "bug", "issue", "slow", "warning", "timeout", "glitch", "failure", "failed"
    );

    private static final List<String> LOW_KEYWORDS = List.of(
            "question", "feedback", "inquiry", "minor", "docs", "documentation", "cosmetic", "request", "typo"
    );

    public TriageResult evaluatePriority(String title, String description, String requestedPriority) {
        if (requestedPriority != null && !requestedPriority.isBlank() && !"AUTO".equalsIgnoreCase(requestedPriority.trim())) {
            String sanitized = requestedPriority.toUpperCase().trim();
            if (sanitized.matches("LOW|MEDIUM|HIGH|CRITICAL")) {
                return new TriageResult(sanitized, false, null);
            }
        }

        String combinedText = ((title != null ? title : "") + " " + (description != null ? description : "")).toLowerCase();

        // 1. Check Critical Keywords
        String criticalMatch = findMatchingKeyword(combinedText, CRITICAL_KEYWORDS);
        if (criticalMatch != null) {
            return new TriageResult("CRITICAL", true, criticalMatch);
        }

        // 2. Check High Keywords
        String highMatch = findMatchingKeyword(combinedText, HIGH_KEYWORDS);
        if (highMatch != null) {
            return new TriageResult("HIGH", true, highMatch);
        }

        // 3. Check Medium Keywords
        String mediumMatch = findMatchingKeyword(combinedText, MEDIUM_KEYWORDS);
        if (mediumMatch != null) {
            return new TriageResult("MEDIUM", true, mediumMatch);
        }

        // 4. Check Low Keywords
        String lowMatch = findMatchingKeyword(combinedText, LOW_KEYWORDS);
        if (lowMatch != null) {
            return new TriageResult("LOW", true, lowMatch);
        }

        // Fallback default: Not detected from keywords
        return new TriageResult("MEDIUM", false, null);
    }

    private String findMatchingKeyword(String text, List<String> keywords) {
        for (String keyword : keywords) {
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return keyword;
            }
        }
        return null;
    }
}
