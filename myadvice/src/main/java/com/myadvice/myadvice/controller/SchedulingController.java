package com.myadvice.myadvice.controller;

import com.myadvice.myadvice.entity.StudentSchedule;
import com.myadvice.myadvice.service.SchedulingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
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

    @PostMapping("/sections")
    public ResponseEntity<ApiDtoFactory.SectionDto> addSection(@RequestBody SectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiDtoFactory.toSectionDto(schedulingService.addSection(
                        request.courseCode(),
                        request.termId(),
                        request.sectionNumber(),
                        request.instructorUserId(),
                        request.building(),
                        request.room()
                ))
        );
    }

    @PutMapping("/sections/{sectionId}")
    public ResponseEntity<ApiDtoFactory.SectionDto> updateSection(@PathVariable Integer sectionId,
                                                                  @RequestBody SectionRequest request) {
        return ResponseEntity.ok(ApiDtoFactory.toSectionDto(
                schedulingService.updateSection(
                        sectionId,
                        request.courseCode(),
                        request.termId(),
                        request.sectionNumber(),
                        request.instructorUserId(),
                        request.building(),
                        request.room()
                )
        ));
    }

    @DeleteMapping("/sections/{sectionId}")
    public ResponseEntity<Void> deleteSection(@PathVariable Integer sectionId) {
        schedulingService.deleteSection(sectionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/sections/{sectionId}/meetings")
    public ResponseEntity<List<ApiDtoFactory.SectionMeetingDto>> replaceMeetings(@PathVariable Integer sectionId,
                                                                                 @RequestBody List<MeetingRequest> request) {
        List<SchedulingService.MeetingUpdate> updates = request.stream()
                .map(meeting -> new SchedulingService.MeetingUpdate(
                        meeting.dayOfWeek(),
                        meeting.startTime(),
                        meeting.endTime()
                ))
                .toList();

        return ResponseEntity.ok(schedulingService.replaceMeetings(sectionId, updates).stream()
                .map(ApiDtoFactory::toSectionMeetingDto)
                .toList());
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
    public record SectionRequest(String courseCode, Integer termId, String sectionNumber,
                                 Integer instructorUserId, String building, String room) {}
    public record MeetingRequest(com.myadvice.myadvice.entity.SectionMeeting.DayOfWeek dayOfWeek,
                                 LocalTime startTime,
                                 LocalTime endTime) {}
}
