package com.myadvice.myadvice.service;

import com.myadvice.myadvice.entity.*;
import com.myadvice.myadvice.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminService {

    private final CourseRepository courseRepository;
    private final CoursePrerequisiteRepository prerequisiteRepository;
    private final SectionRepository sectionRepository;
    private final SectionMeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final SigninRequestRepository signinRequestRepository;
    private final TimetableChangeRepository timetableChangeRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    public AdminService(CourseRepository courseRepository,
                        CoursePrerequisiteRepository prerequisiteRepository,
                        SectionRepository sectionRepository,
                        SectionMeetingRepository meetingRepository,
                        UserRepository userRepository,
                        SigninRequestRepository signinRequestRepository,
                        TimetableChangeRepository timetableChangeRepository,
                        StudentRepository studentRepository,
                        FacultyRepository facultyRepository) {
        this.courseRepository = courseRepository;
        this.prerequisiteRepository = prerequisiteRepository;
        this.sectionRepository = sectionRepository;
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
        this.signinRequestRepository = signinRequestRepository;
        this.timetableChangeRepository = timetableChangeRepository;
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    // ===== COURSE MANAGEMENT =====

    // Add a new course
    public Course addCourse(String courseCode, String courseName, Integer credits, String description) {
        Course course = new Course();
        course.setCourseCode(courseCode);
        course.setCourseName(courseName);
        course.setCredits(credits);
        course.setDescription(description);
        return courseRepository.save(course);
    }

    // Get all courses
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // Delete a course
    public void deleteCourse(String courseCode) {
        courseRepository.deleteById(courseCode);
    }

    // ===== PREREQUISITE MANAGEMENT =====

    // Add a prerequisite
    public CoursePrerequisite addPrerequisite(String courseCode, String prereqCourseCode) {
        CoursePrerequisite prereq = new CoursePrerequisite();
        prereq.setCourseCode(courseCode);
        prereq.setPrereqCourseCode(prereqCourseCode);
        return prerequisiteRepository.save(prereq);
    }

    // Get prerequisites for a course
    public List<CoursePrerequisite> getPrerequisites(String courseCode) {
        return prerequisiteRepository.findByCourseCode(courseCode);
    }

    // Remove a prerequisite
    public void removePrerequisite(String courseCode, String prereqCourseCode) {
        CoursePrerequisite.PrerequisiteId id = new CoursePrerequisite.PrerequisiteId(courseCode, prereqCourseCode);
        prerequisiteRepository.deleteById(id);
    }

    // ===== SECTION MANAGEMENT =====

    // Add a section
    public Section addSection(String courseCode, Integer termId, String sectionNumber,
                               Integer instructorUserId, String building, String room) {
        Section section = new Section();
        section.setCourseCode(courseCode);
        section.setTermId(termId);
        section.setSectionNumber(sectionNumber);
        section.setInstructorUserId(instructorUserId);
        section.setBuilding(building);
        section.setRoom(room);
        return sectionRepository.save(section);
    }

    // Get sections by course and term
    public List<Section> getSections(String courseCode, Integer termId) {
        return sectionRepository.findByCourseCodeAndTermId(courseCode, termId);
    }

    // Delete a section
    public void deleteSection(Integer sectionId) {
        sectionRepository.deleteById(sectionId);
    }

    // ===== USER APPROVAL =====

    // Get pending signin requests
    public List<SigninRequest> getPendingRequests() {
        return signinRequestRepository.findByDecision(SigninRequest.Decision.PENDING);
    }

    // Approve a user
    public void approveUser(Integer requestId, Integer handledByUserId) {
        SigninRequest request = signinRequestRepository.findById(requestId).orElse(null);
        if (request != null) {
            request.setDecision(SigninRequest.Decision.APPROVED);
            request.setHandledByUserId(handledByUserId);
            signinRequestRepository.save(request);

            // Also update user status
            User user = userRepository.findById(request.getUserId()).orElse(null);
            if (user != null) {
                user.setStatus(User.Status.APPROVED);
                userRepository.save(user);
            }
        }
    }

    // Deny a user
    public void denyUser(Integer requestId, Integer handledByUserId, String note) {
        SigninRequest request = signinRequestRepository.findById(requestId).orElse(null);
        if (request != null) {
            request.setDecision(SigninRequest.Decision.DENIED);
            request.setHandledByUserId(handledByUserId);
            request.setDecisionNote(note);
            signinRequestRepository.save(request);

            User user = userRepository.findById(request.getUserId()).orElse(null);
            if (user != null) {
                user.setStatus(User.Status.DENIED);
                userRepository.save(user);
            }
        }
    }

    // ===== PROFILE MANAGEMENT =====

    // Get all students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Get all faculty
    public List<Faculty> getAllFaculty() {
        return facultyRepository.findAll();
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ===== TIMETABLE AUDIT =====

    // Log a timetable change
    public TimetableChange logChange(Integer adminUserId, Integer sectionId,
                                      String changeType, String oldValue, String newValue) {
        TimetableChange change = new TimetableChange();
        change.setAdminUserId(adminUserId);
        change.setSectionId(sectionId);
        change.setChangeType(changeType);
        change.setOldValue(oldValue);
        change.setNewValue(newValue);
        return timetableChangeRepository.save(change);
    }

    // Get timetable change history
    public List<TimetableChange> getChangeHistory() {
        return timetableChangeRepository.findAll();
    }
}
