package com.myadvice.myadvice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "advisors")
public class Advisor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "advisor_id")
    private Integer advisorId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "active")
    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private AdvisingCategory category;

    public Advisor() {}

    // Getters and Setters
    public Integer getAdvisorId() { return advisorId; }
    public void setAdvisorId(Integer advisorId) { this.advisorId = advisorId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public User getUser() { return user; }
    public AdvisingCategory getCategory() { return category; }
}
