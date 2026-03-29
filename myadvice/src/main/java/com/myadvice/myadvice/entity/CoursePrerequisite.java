package com.myadvice.myadvice.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "course_prerequisites")
@IdClass(CoursePrerequisite.PrerequisiteId.class)
public class CoursePrerequisite {

    @Id
    @Column(name = "course_code", length = 20)
    private String courseCode;

    @Id
    @Column(name = "prereq_course_code", length = 20)
    private String prereqCourseCode;

    @ManyToOne
    @JoinColumn(name = "course_code", insertable = false, updatable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "prereq_course_code", insertable = false, updatable = false)
    private Course prerequisite;

    public CoursePrerequisite() {}

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getPrereqCourseCode() { return prereqCourseCode; }
    public void setPrereqCourseCode(String prereqCourseCode) { this.prereqCourseCode = prereqCourseCode; }

    public Course getCourse() { return course; }
    public Course getPrerequisite() { return prerequisite; }

    public static class PrerequisiteId implements Serializable {
        private String courseCode;
        private String prereqCourseCode;

        public PrerequisiteId() {}

        public PrerequisiteId(String courseCode, String prereqCourseCode) {
            this.courseCode = courseCode;
            this.prereqCourseCode = prereqCourseCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PrerequisiteId that = (PrerequisiteId) o;
            return Objects.equals(courseCode, that.courseCode) && Objects.equals(prereqCourseCode, that.prereqCourseCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(courseCode, prereqCourseCode);
        }
    }
}
