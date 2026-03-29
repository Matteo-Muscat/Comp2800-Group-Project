package com.myadvice.myadvice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sections")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private Integer sectionId;

    @Column(name = "course_code", nullable = false, length = 20)
    private String courseCode;

    @Column(name = "term_id", nullable = false)
    private Integer termId;

    @Column(name = "section_number", length = 10)
    private String sectionNumber;

    @Column(name = "instructor_user_id")
    private Integer instructorUserId;

    @Column(name = "building", length = 50)
    private String building;

    @Column(name = "room", length = 20)
    private String room;

    @ManyToOne
    @JoinColumn(name = "course_code", insertable = false, updatable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "term_id", insertable = false, updatable = false)
    private Term term;

    @ManyToOne
    @JoinColumn(name = "instructor_user_id", insertable = false, updatable = false)
    private User instructor;

    public Section() {}

    public Integer getSectionId() { return sectionId; }
    public void setSectionId(Integer sectionId) { this.sectionId = sectionId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public Integer getTermId() { return termId; }
    public void setTermId(Integer termId) { this.termId = termId; }

    public String getSectionNumber() { return sectionNumber; }
    public void setSectionNumber(String sectionNumber) { this.sectionNumber = sectionNumber; }

    public Integer getInstructorUserId() { return instructorUserId; }
    public void setInstructorUserId(Integer instructorUserId) { this.instructorUserId = instructorUserId; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public Course getCourse() { return course; }
    public Term getTerm() { return term; }
    public User getInstructor() { return instructor; }
}
