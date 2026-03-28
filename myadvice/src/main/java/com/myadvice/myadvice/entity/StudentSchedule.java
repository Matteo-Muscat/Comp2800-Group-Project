package com.myadvice.myadvice.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "student_schedule")
@IdClass(StudentSchedule.ScheduleId.class)
public class StudentSchedule {

    @Id
    @Column(name = "student_id")
    private Integer studentId;

    @Id
    @Column(name = "term_id")
    private Integer termId;

    @Id
    @Column(name = "section_id")
    private Integer sectionId;

    @ManyToOne
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "term_id", insertable = false, updatable = false)
    private Term term;

    @ManyToOne
    @JoinColumn(name = "section_id", insertable = false, updatable = false)
    private Section section;

    public StudentSchedule() {}

    // Getters and Setters
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public Integer getTermId() { return termId; }
    public void setTermId(Integer termId) { this.termId = termId; }

    public Integer getSectionId() { return sectionId; }
    public void setSectionId(Integer sectionId) { this.sectionId = sectionId; }

    public Student getStudent() { return student; }
    public Term getTerm() { return term; }
    public Section getSection() { return section; }

    // Composite Key class
    public static class ScheduleId implements Serializable {
        private Integer studentId;
        private Integer termId;
        private Integer sectionId;

        public ScheduleId() {}

        public ScheduleId(Integer studentId, Integer termId, Integer sectionId) {
            this.studentId = studentId;
            this.termId = termId;
            this.sectionId = sectionId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ScheduleId that = (ScheduleId) o;
            return Objects.equals(studentId, that.studentId) && Objects.equals(termId, that.termId) && Objects.equals(sectionId, that.sectionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(studentId, termId, sectionId);
        }
    }
}
