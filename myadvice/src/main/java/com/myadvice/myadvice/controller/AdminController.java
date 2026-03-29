package com.myadvice.myadvice.controller;

import com.myadvice.myadvice.entity.Course;
import com.myadvice.myadvice.entity.CoursePrerequisite;
import com.myadvice.myadvice.entity.Section;
import com.myadvice.myadvice.entity.TimetableChange;
import com.myadvice.myadvice.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/courses")
    public List<ApiDtoFactory.CourseDto> getCourses() {
        return adminService.getAllCourses().stream().map(ApiDtoFactory::toCourseDto).toList();
    }

    @PostMapping("/courses")
    public ResponseEntity<ApiDtoFactory.CourseDto> addCourse(@RequestBody AddCourseRequest request) {
        Course course = adminService.addCourse(
                request.courseCode(),
                request.courseName(),
                request.credits(),
                request.description()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiDtoFactory.toCourseDto(course));
    }

    @DeleteMapping("/courses/{courseCode}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String courseCode) {
        adminService.deleteCourse(courseCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/courses/{courseCode}/prerequisites")
    public List<ApiDtoFactory.CoursePrerequisiteDto> getPrerequisites(@PathVariable String courseCode) {
        return adminService.getPrerequisites(courseCode).stream()
                .map(ApiDtoFactory::toCoursePrerequisiteDto)
                .toList();
    }

    @PostMapping("/courses/{courseCode}/prerequisites")
    public ResponseEntity<ApiDtoFactory.CoursePrerequisiteDto> addPrerequisite(@PathVariable String courseCode,
                                                                               @RequestBody AddPrerequisiteRequest request) {
        CoursePrerequisite prerequisite = adminService.addPrerequisite(courseCode, request.prereqCourseCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiDtoFactory.toCoursePrerequisiteDto(prerequisite));
    }

    @DeleteMapping("/courses/{courseCode}/prerequisites/{prereqCourseCode}")
    public ResponseEntity<Void> removePrerequisite(@PathVariable String courseCode,
                                                   @PathVariable String prereqCourseCode) {
        adminService.removePrerequisite(courseCode, prereqCourseCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sections")
    public List<ApiDtoFactory.SectionDto> getSections(@RequestParam String courseCode,
                                                      @RequestParam Integer termId) {
        return adminService.getSections(courseCode, termId).stream()
                .map(ApiDtoFactory::toSectionDto)
                .toList();
    }

    @PostMapping("/sections")
    public ResponseEntity<ApiDtoFactory.SectionDto> addSection(@RequestBody AddSectionRequest request) {
        Section section = adminService.addSection(
                request.courseCode(),
                request.termId(),
                request.sectionNumber(),
                request.instructorUserId(),
                request.building(),
                request.room()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiDtoFactory.toSectionDto(section));
    }

    @DeleteMapping("/sections/{sectionId}")
    public ResponseEntity<Void> deleteSection(@PathVariable Integer sectionId) {
        adminService.deleteSection(sectionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/signin-requests/pending")
    public List<ApiDtoFactory.SigninRequestDto> getPendingRequests() {
        return adminService.getPendingRequests().stream()
                .map(ApiDtoFactory::toSigninRequestDto)
                .toList();
    }

    @PostMapping("/signin-requests/{requestId}/approve")
    public ResponseEntity<Void> approveUser(@PathVariable Integer requestId,
                                            @RequestBody HandleRequest request) {
        adminService.approveUser(requestId, request.handledByUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin-requests/{requestId}/deny")
    public ResponseEntity<Void> denyUser(@PathVariable Integer requestId,
                                         @RequestBody DenyRequest request) {
        adminService.denyUser(requestId, request.handledByUserId(), request.note());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/students")
    public List<ApiDtoFactory.StudentDto> getStudents() {
        return adminService.getAllStudents().stream().map(ApiDtoFactory::toStudentDto).toList();
    }

    @GetMapping("/faculty")
    public List<ApiDtoFactory.FacultyDto> getFaculty() {
        return adminService.getAllFaculty().stream().map(ApiDtoFactory::toFacultyDto).toList();
    }

    @GetMapping("/users")
    public List<ApiDtoFactory.UserDto> getUsers() {
        return adminService.getAllUsers().stream().map(ApiDtoFactory::toUserDto).toList();
    }

    @PostMapping("/timetable-changes")
    public ResponseEntity<ApiDtoFactory.TimetableChangeDto> logChange(@RequestBody LogChangeRequest request) {
        TimetableChange change = adminService.logChange(
                request.adminUserId(),
                request.sectionId(),
                request.changeType(),
                request.oldValue(),
                request.newValue()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiDtoFactory.toTimetableChangeDto(change));
    }

    @GetMapping("/timetable-changes")
    public List<ApiDtoFactory.TimetableChangeDto> getChangeHistory() {
        return adminService.getChangeHistory().stream()
                .map(ApiDtoFactory::toTimetableChangeDto)
                .toList();
    }

    public record AddCourseRequest(String courseCode, String courseName, Integer credits, String description) {}
    public record AddPrerequisiteRequest(String prereqCourseCode) {}
    public record AddSectionRequest(String courseCode, Integer termId, String sectionNumber,
                                    Integer instructorUserId, String building, String room) {}
    public record HandleRequest(Integer handledByUserId) {}
    public record DenyRequest(Integer handledByUserId, String note) {}
    public record LogChangeRequest(Integer adminUserId, Integer sectionId, String changeType,
                                   String oldValue, String newValue) {}
}
