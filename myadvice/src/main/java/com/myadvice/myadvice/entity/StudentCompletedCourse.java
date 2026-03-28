package com.myadvice.myadvice.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "student_completed_courses")
@IdClass(StudentCompletedCourse.StudentCourseId.class)
public class StudentCompletedCourse {

    @Id
    @Column(name = "student_id")
    private Integer studentId;

    @Id
    @Column(name = "course_code", length = 20)
    private String courseCode;

    @Column(name = "term_id")
    private Integer termId;

    @Column(name = "grade", length = 5)
    private String grade;

    @ManyToOne
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_code", insertable = false, updatable = false)
    private Course course;

    public StudentCompletedCourse() {}

    // Getters and Setters
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public Integer getTermId() { return termId; }
    public void setTermId(Integer termId) { this.termId = termId; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public Student getStudent() { return student; }
    public Course getCourse() { return course; }

    // Composite Key class
    public static class StudentCourseId implements Serializable {
        private Integer studentId;
        private String courseCode;

        public StudentCourseId() {}

        public StudentCourseId(Integer studentId, String courseCode) {
            this.studentId = studentId;
            this.courseCode = courseCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StudentCourseId that = (StudentCourseId) o;
            return Objects.equals(studentId, that.studentId) && Objects.equals(courseCode, that.courseCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(studentId, courseCode);
        }
    }
}
