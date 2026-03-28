package com.myadvice.myadvice.service;

import com.myadvice.myadvice.entity.*;
import com.myadvice.myadvice.repository.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurriculumService {

    private final CourseRepository courseRepository;
    private final CoursePrerequisiteRepository prerequisiteRepository;
    private final StudentCompletedCourseRepository completedCourseRepository;

    public CurriculumService(CourseRepository courseRepository,
                              CoursePrerequisiteRepository prerequisiteRepository,
                              StudentCompletedCourseRepository completedCourseRepository) {
        this.courseRepository = courseRepository;
        this.prerequisiteRepository = prerequisiteRepository;
        this.completedCourseRepository = completedCourseRepository;
    }

    // Get all courses a student has completed
    public List<StudentCompletedCourse> getCompletedCourses(Integer studentId) {
        return completedCourseRepository.findByStudentId(studentId);
    }

    // Get all available courses
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // Get prerequisites for a specific course
    public List<CoursePrerequisite> getPrerequisitesForCourse(String courseCode) {
        return prerequisiteRepository.findByCourseCode(courseCode);
    }

    // Check if a student has met prerequisites for a course
    public boolean hasMetPrerequisites(Integer studentId, String courseCode) {
        List<CoursePrerequisite> prereqs = prerequisiteRepository.findByCourseCode(courseCode);
        List<StudentCompletedCourse> completed = completedCourseRepository.findByStudentId(studentId);

        List<String> completedCodes = completed.stream()
                .map(StudentCompletedCourse::getCourseCode)
                .collect(Collectors.toList());

        for (CoursePrerequisite prereq : prereqs) {
            if (!completedCodes.contains(prereq.getPrereqCourseCode())) {
                return false;
            }
        }
        return true;
    }

    // Get courses a student is eligible to take
    public List<Course> getEligibleCourses(Integer studentId) {
        List<Course> allCourses = courseRepository.findAll();
        List<StudentCompletedCourse> completed = completedCourseRepository.findByStudentId(studentId);

        List<String> completedCodes = completed.stream()
                .map(StudentCompletedCourse::getCourseCode)
                .collect(Collectors.toList());

        List<Course> eligible = new ArrayList<>();
        for (Course course : allCourses) {
            // Skip if already completed
            if (completedCodes.contains(course.getCourseCode())) continue;
            // Check if prerequisites are met
            if (hasMetPrerequisites(studentId, course.getCourseCode())) {
                eligible.add(course);
            }
        }
        return eligible;
    }

    // Get courses a student still needs (not yet completed)
    public List<Course> getRemainingCourses(Integer studentId) {
        List<Course> allCourses = courseRepository.findAll();
        List<StudentCompletedCourse> completed = completedCourseRepository.findByStudentId(studentId);

        List<String> completedCodes = completed.stream()
                .map(StudentCompletedCourse::getCourseCode)
                .collect(Collectors.toList());

        return allCourses.stream()
                .filter(c -> !completedCodes.contains(c.getCourseCode()))
                .collect(Collectors.toList());
    }
}
