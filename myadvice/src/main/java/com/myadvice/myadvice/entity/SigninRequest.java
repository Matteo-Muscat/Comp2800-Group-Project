package com.myadvice.myadvice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "signin_requests")
public class SigninRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer requestId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "handled_by_user_id")
    private Integer handledByUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision")
    private Decision decision = Decision.PENDING;

    @Column(name = "decision_note", columnDefinition = "TEXT")
    private String decisionNote;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "handled_by_user_id", insertable = false, updatable = false)
    private User handledBy;

    public enum Decision { PENDING, APPROVED, DENIED }

    public SigninRequest() {}

    // Getters and Setters
    public Integer getRequestId() { return requestId; }
    public void setRequestId(Integer requestId) { this.requestId = requestId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public Integer getHandledByUserId() { return handledByUserId; }
    public void setHandledByUserId(Integer handledByUserId) { this.handledByUserId = handledByUserId; }

    public Decision getDecision() { return decision; }
    public void setDecision(Decision decision) { this.decision = decision; }

    public String getDecisionNote() { return decisionNote; }
    public void setDecisionNote(String decisionNote) { this.decisionNote = decisionNote; }

    public User getUser() { return user; }
    public User getHandledBy() { return handledBy; }
}
