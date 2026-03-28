package com.myadvice.myadvice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "timetable_changes")
public class TimetableChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "change_id")
    private Integer changeId;

    @Column(name = "admin_user_id", nullable = false)
    private Integer adminUserId;

    @Column(name = "section_id", nullable = false)
    private Integer sectionId;

    @Column(name = "change_type", nullable = false, length = 50)
    private String changeType;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "admin_user_id", insertable = false, updatable = false)
    private User adminUser;

    @ManyToOne
    @JoinColumn(name = "section_id", insertable = false, updatable = false)
    private Section section;

    public TimetableChange() {}


    public Integer getChangeId() { return changeId; }
    public void setChangeId(Integer changeId) { this.changeId = changeId; }

    public Integer getAdminUserId() { return adminUserId; }
    public void setAdminUserId(Integer adminUserId) { this.adminUserId = adminUserId; }

    public Integer getSectionId() { return sectionId; }
    public void setSectionId(Integer sectionId) { this.sectionId = sectionId; }

    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getAdminUser() { return adminUser; }
    public Section getSection() { return section; }
}
