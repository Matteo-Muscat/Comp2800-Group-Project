package com.myadvice.myadvice.service;

import com.myadvice.myadvice.entity.*;
import com.myadvice.myadvice.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SchedulingService {

    private final SectionRepository sectionRepository;
    private final SectionMeetingRepository meetingRepository;
    private final StudentScheduleRepository scheduleRepository;
    private final TermRepository termRepository;

    public SchedulingService(SectionRepository sectionRepository,
                              SectionMeetingRepository meetingRepository,
                              StudentScheduleRepository scheduleRepository,
                              TermRepository termRepository) {
        this.sectionRepository = sectionRepository;
        this.meetingRepository = meetingRepository;
        this.scheduleRepository = scheduleRepository;
        this.termRepository = termRepository;
    }

    // Get all terms
    public List<Term> getAllTerms() {
        return termRepository.findAll();
    }

    // Get sections for a course in a term
    public List<Section> getSectionsForCourse(String courseCode, Integer termId) {
        return sectionRepository.findByCourseCodeAndTermId(courseCode, termId);
    }

    // Get all sections in a term
    public List<Section> getAllSectionsInTerm(Integer termId) {
        return sectionRepository.findByTermId(termId);
    }

    // Get meeting times for a section
    public List<SectionMeeting> getMeetingTimes(Integer sectionId) {
        return meetingRepository.findBySectionId(sectionId);
    }

    // Add a section to student's schedule
    public StudentSchedule addToSchedule(Integer studentId, Integer termId, Integer sectionId) {
        StudentSchedule schedule = new StudentSchedule();
        schedule.setStudentId(studentId);
        schedule.setTermId(termId);
        schedule.setSectionId(sectionId);
        return scheduleRepository.save(schedule);
    }

    // Remove a section from student's schedule
    public void removeFromSchedule(Integer studentId, Integer termId, Integer sectionId) {
        StudentSchedule.ScheduleId id = new StudentSchedule.ScheduleId(studentId, termId, sectionId);
        scheduleRepository.deleteById(id);
    }

    // Get student's schedule for a term
    public List<StudentSchedule> getStudentSchedule(Integer studentId, Integer termId) {
        return scheduleRepository.findByStudentIdAndTermId(studentId, termId);
    }

    // Get all students enrolled in a section
    public List<StudentSchedule> getEnrolledStudents(Integer sectionId) {
        return scheduleRepository.findBySectionId(sectionId);
    }
}
