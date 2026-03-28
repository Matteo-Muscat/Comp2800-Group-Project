package com.myadvice.myadvice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointment_suggestions")
public class AppointmentSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "suggestion_id")
    private Integer suggestionId;

    @Column(name = "appointment_id", nullable = false)
    private Integer appointmentId;

    @Column(name = "suggested_date", nullable = false)
    private LocalDate suggestedDate;

    @Column(name = "suggested_start_time", nullable = false)
    private LocalTime suggestedStartTime;

    @Column(name = "suggested_end_time")
    private LocalTime suggestedEndTime;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "appointment_id", insertable = false, updatable = false)
    private Appointment appointment;

    public AppointmentSuggestion() {}

    // Getters and Setters
    public Integer getSuggestionId() { return suggestionId; }
    public void setSuggestionId(Integer suggestionId) { this.suggestionId = suggestionId; }

    public Integer getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Integer appointmentId) { this.appointmentId = appointmentId; }

    public LocalDate getSuggestedDate() { return suggestedDate; }
    public void setSuggestedDate(LocalDate suggestedDate) { this.suggestedDate = suggestedDate; }

    public LocalTime getSuggestedStartTime() { return suggestedStartTime; }
    public void setSuggestedStartTime(LocalTime suggestedStartTime) { this.suggestedStartTime = suggestedStartTime; }

    public LocalTime getSuggestedEndTime() { return suggestedEndTime; }
    public void setSuggestedEndTime(LocalTime suggestedEndTime) { this.suggestedEndTime = suggestedEndTime; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Appointment getAppointment() { return appointment; }
}
