package com.myadvice.myadvice.controller;

import com.myadvice.myadvice.service.CurriculumService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/curriculum")
public class CurriculumController {

    private final CurriculumService curriculumService;

    public CurriculumController(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    @GetMapping("/courses")
    public List<ApiDtoFactory.CourseDto> getAllCourses() {
        return curriculumService.getAllCourses().stream().map(ApiDtoFactory::toCourseDto).toList();
    }

    @GetMapping("/courses/{courseCode}/prerequisites")
    public List<ApiDtoFactory.CoursePrerequisiteDto> getPrerequisites(@PathVariable String courseCode) {
        return curriculumService.getPrerequisitesForCourse(courseCode).stream()
                .map(ApiDtoFactory::toCoursePrerequisiteDto)
                .toList();
    }

    @GetMapping("/students/{studentId}/completed")
    public List<ApiDtoFactory.CompletedCourseDto> getCompletedCourses(@PathVariable Integer studentId) {
        return curriculumService.getCompletedCourses(studentId).stream()
                .map(ApiDtoFactory::toCompletedCourseDto)
                .toList();
    }

    @GetMapping("/students/{studentId}/eligible")
    public List<ApiDtoFactory.CourseDto> getEligibleCourses(@PathVariable Integer studentId) {
        return curriculumService.getEligibleCourses(studentId).stream()
                .map(ApiDtoFactory::toCourseDto)
                .toList();
    }

    @GetMapping("/students/{studentId}/remaining")
    public List<ApiDtoFactory.CourseDto> getRemainingCourses(@PathVariable Integer studentId) {
        return curriculumService.getRemainingCourses(studentId).stream()
                .map(ApiDtoFactory::toCourseDto)
                .toList();
    }

    @GetMapping("/students/{studentId}/check")
    public Map<String, Object> checkPrerequisites(@PathVariable Integer studentId,
                                                  @RequestParam String courseCode) {
        return Map.of(
                "studentId", studentId,
                "courseCode", courseCode,
                "met", curriculumService.hasMetPrerequisites(studentId, courseCode),
                "prerequisites", curriculumService.getPrerequisitesForCourse(courseCode).stream()
                        .map(ApiDtoFactory::toCoursePrerequisiteDto)
                        .toList(),
                "completedCourses", curriculumService.getCompletedCourses(studentId).stream()
                        .map(ApiDtoFactory::toCompletedCourseDto)
                        .toList()
        );
    }
}
