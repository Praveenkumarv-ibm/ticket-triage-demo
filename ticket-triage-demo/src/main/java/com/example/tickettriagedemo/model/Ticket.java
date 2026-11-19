package com.example.tickettriagedemo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
    @Column(nullable = false, length = 150)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
    @Column(nullable = false, length = 2000)
    private String description;

    @NotBlank(message = "Customer email is required")
    @Size(max = 254, message = "Email address must not exceed 254 characters")
    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Please provide a valid email address (e.g., user@domain.com)")
    @Column(nullable = false, length = 254)
    private String customerEmail;

    @Pattern(regexp = "^(AUTO|LOW|MEDIUM|HIGH|CRITICAL)$", message = "Priority must be AUTO, LOW, MEDIUM, HIGH, or CRITICAL")
    @Column(nullable = false, length = 20)
    private String priority = "AUTO";

    @Pattern(regexp = "^(OPEN|IN_PROGRESS|RESOLVED|CLOSED)$", message = "Status must be OPEN, IN_PROGRESS, RESOLVED, or CLOSED")
    @Column(nullable = false, length = 20)
    private String status = "OPEN";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Ticket() {
        this.priority = "AUTO";
        this.status = "OPEN";
    }

    public Ticket(String title, String description, String customerEmail) {
        this(title, description, customerEmail, "AUTO");
    }

    public Ticket(String title, String description, String customerEmail, String priority) {
        this.title = title != null ? title.trim() : null;
        this.description = description != null ? description.trim() : null;
        this.customerEmail = customerEmail != null ? customerEmail.trim() : null;
        this.priority = (priority != null && !priority.isBlank()) ? priority.toUpperCase().trim() : "AUTO";
        this.status = "OPEN";
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null || this.status.isBlank()) {
            this.status = "OPEN";
        }
        if (this.priority == null || this.priority.isBlank() || "AUTO".equalsIgnoreCase(this.priority)) {
            this.priority = "MEDIUM";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title != null ? title.trim() : null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail != null ? customerEmail.trim() : null;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority != null ? priority.toUpperCase().trim() : "AUTO";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status != null ? status.toUpperCase().trim() : "OPEN";
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
