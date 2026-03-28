package com.myadvice.myadvice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "faculty")
public class Faculty {

    @Id
    @Column(name = "faculty_id")
    private Integer facultyId;

    @OneToOne
    @JoinColumn(name = "faculty_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "office_location", length = 100)
    private String officeLocation;

    @Column(name = "research_interests", columnDefinition = "TEXT")
    private String researchInterests;

    @Column(name = "email", length = 100)
    private String email;

    public Faculty() {}


    public Integer getFacultyId() { return facultyId; }
    public void setFacultyId(Integer facultyId) { this.facultyId = facultyId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getOfficeLocation() { return officeLocation; }
    public void setOfficeLocation(String officeLocation) { this.officeLocation = officeLocation; }

    public String getResearchInterests() { return researchInterests; }
    public void setResearchInterests(String researchInterests) { this.researchInterests = researchInterests; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
