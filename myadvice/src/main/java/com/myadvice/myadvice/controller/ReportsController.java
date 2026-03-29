package com.myadvice.myadvice.controller;

import com.myadvice.myadvice.service.ReportsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final ReportsService reportsService;

    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @GetMapping("/students")
    public List<ApiDtoFactory.StudentDto> getStudents(@RequestParam(required = false) String major,
                                                      @RequestParam(required = false) String program,
                                                      @RequestParam(required = false) Integer yearLevel) {
        return reportsService.getStudents(major, program, yearLevel).stream()
                .map(ApiDtoFactory::toStudentDto)
                .toList();
    }

    @GetMapping("/faculty")
    public List<ApiDtoFactory.FacultyDto> getFaculty(@RequestParam(required = false) String department) {
        return reportsService.getFaculty(department).stream()
                .map(ApiDtoFactory::toFacultyDto)
                .toList();
    }

    @GetMapping("/students/most-appointments")
    public Map<Integer, Long> getStudentsWithMostAppointments() {
        return reportsService.getStudentsWithMostAppointments();
    }

    @GetMapping("/advisors/most-appointments")
    public Map<Integer, Long> getAdvisorsWithMostAppointments() {
        return reportsService.getAdvisorsWithMostAppointments();
    }

    @GetMapping("/appointments/status-counts")
    public Map<String, Long> getAppointmentCountsByStatus() {
        return reportsService.getAppointmentCountsByStatus();
    }

    @GetMapping("/dashboard")
    public Map<String, Long> getDashboardStats() {
        return reportsService.getDashboardStats();
    }
}
