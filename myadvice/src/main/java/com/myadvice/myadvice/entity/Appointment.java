package com.myadvice.myadvice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Integer appointmentId;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(name = "advisor_id", nullable = false)
    private Integer advisorId;

    @Column(name = "requested_date", nullable = false)
    private LocalDate requestedDate;

    @Column(name = "requested_start_time", nullable = false)
    private LocalTime requestedStartTime;

    @Column(name = "requested_end_time")
    private LocalTime requestedEndTime;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AppointmentStatus status = AppointmentStatus.REQUESTED;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "advisor_id", insertable = false, updatable = false)
    private Advisor advisor;

    public enum AppointmentStatus { REQUESTED, APPROVED, DENIED, CANCELLED }

    public Appointment() {}

    // Getters and Setters
    public Integer getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Integer appointmentId) { this.appointmentId = appointmentId; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public Integer getAdvisorId() { return advisorId; }
    public void setAdvisorId(Integer advisorId) { this.advisorId = advisorId; }

    public LocalDate getRequestedDate() { return requestedDate; }
    public void setRequestedDate(LocalDate requestedDate) { this.requestedDate = requestedDate; }

    public LocalTime getRequestedStartTime() { return requestedStartTime; }
    public void setRequestedStartTime(LocalTime requestedStartTime) { this.requestedStartTime = requestedStartTime; }

    public LocalTime getRequestedEndTime() { return requestedEndTime; }
    public void setRequestedEndTime(LocalTime requestedEndTime) { this.requestedEndTime = requestedEndTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Student getStudent() { return student; }
    public Advisor getAdvisor() { return advisor; }
}
