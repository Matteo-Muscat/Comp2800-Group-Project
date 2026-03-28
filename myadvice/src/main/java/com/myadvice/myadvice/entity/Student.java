package com.myadvice.myadvice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @Column(name = "student_id")
    private Integer studentId;

    @OneToOne
    @JoinColumn(name = "student_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "major", length = 100)
    private String major;

    @Column(name = "program", length = 100)
    private String program;

    @Column(name = "year_level")
    private Integer yearLevel;

    @Column(name = "email", length = 100)
    private String email;

    public Student() {}

   
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }

    public Integer getYearLevel() { return yearLevel; }
    public void setYearLevel(Integer yearLevel) { this.yearLevel = yearLevel; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
