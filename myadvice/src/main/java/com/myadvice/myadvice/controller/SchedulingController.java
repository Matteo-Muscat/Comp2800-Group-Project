package com.myadvice.myadvice.controller;

import com.myadvice.myadvice.entity.StudentSchedule;
import com.myadvice.myadvice.service.SchedulingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scheduling")
public class SchedulingController {

    private final SchedulingService schedulingService;

    public SchedulingController(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }

    @GetMapping("/terms")
    public List<ApiDtoFactory.TermDto> getTerms() {
        return schedulingService.getAllTerms().stream().map(ApiDtoFactory::toTermDto).toList();
    }

    @GetMapping("/terms/{termId}/sections")
    public List<ApiDtoFactory.SectionDto> getSectionsInTerm(@PathVariable Integer termId,
                                                            @RequestParam(required = false) String courseCode) {
        if (courseCode != null && !courseCode.isBlank()) {
            return schedulingService.getSectionsForCourse(courseCode, termId).stream()
                    .map(ApiDtoFactory::toSectionDto)
                    .toList();
        }
        return schedulingService.getAllSectionsInTerm(termId).stream()
                .map(ApiDtoFactory::toSectionDto)
                .toList();
    }

    @GetMapping("/sections/{sectionId}/meetings")
    public List<ApiDtoFactory.SectionMeetingDto> getMeetings(@PathVariable Integer sectionId) {
        return schedulingService.getMeetingTimes(sectionId).stream()
                .map(ApiDtoFactory::toSectionMeetingDto)
                .toList();
    }

    @PostMapping("/schedule")
    public ResponseEntity<ApiDtoFactory.StudentScheduleDto> addToSchedule(@RequestBody AddScheduleRequest request) {
        StudentSchedule schedule = schedulingService.addToSchedule(
                request.studentId(),
                request.termId(),
                request.sectionId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiDtoFactory.toStudentScheduleDto(schedule));
    }

    @DeleteMapping("/schedule")
    public ResponseEntity<Void> removeFromSchedule(@RequestParam Integer studentId,
                                                   @RequestParam Integer termId,
                                                   @RequestParam Integer sectionId) {
        schedulingService.removeFromSchedule(studentId, termId, sectionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/students/{studentId}/schedule")
    public List<ApiDtoFactory.StudentScheduleDto> getStudentSchedule(@PathVariable Integer studentId,
                                                                     @RequestParam Integer termId) {
        return schedulingService.getStudentSchedule(studentId, termId).stream()
                .map(ApiDtoFactory::toStudentScheduleDto)
                .toList();
    }

    @GetMapping("/sections/{sectionId}/students")
    public List<ApiDtoFactory.StudentScheduleDto> getEnrolledStudents(@PathVariable Integer sectionId) {
        return schedulingService.getEnrolledStudents(sectionId).stream()
                .map(ApiDtoFactory::toStudentScheduleDto)
                .toList();
    }

    public record AddScheduleRequest(Integer studentId, Integer termId, Integer sectionId) {}
}
