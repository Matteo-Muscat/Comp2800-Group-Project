package com.myadvice.myadvice.service;

import com.myadvice.myadvice.entity.*;
import com.myadvice.myadvice.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportsService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final AppointmentRepository appointmentRepository;
    private final AdvisorRepository advisorRepository;
    private final UserRepository userRepository;

    public ReportsService(StudentRepository studentRepository,
                           FacultyRepository facultyRepository,
                           AppointmentRepository appointmentRepository,
                           AdvisorRepository advisorRepository,
                           UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.appointmentRepository = appointmentRepository;
        this.advisorRepository = advisorRepository;
        this.userRepository = userRepository;
    }

    // List all students with optional filters
    public List<Student> getStudents(String major, String program, Integer yearLevel) {
        if (major != null) return studentRepository.findByMajor(major);
        if (program != null) return studentRepository.findByProgram(program);
        if (yearLevel != null) return studentRepository.findByYearLevel(yearLevel);
        return studentRepository.findAll();
    }

    // List all faculty with optional department filter
    public List<Faculty> getFaculty(String department) {
        if (department != null) return facultyRepository.findByDepartment(department);
        return facultyRepository.findAll();
    }

    // Get students with most appointments
    public Map<Integer, Long> getStudentsWithMostAppointments() {
        List<Appointment> allAppointments = appointmentRepository.findAll();
        return allAppointments.stream()
                .collect(Collectors.groupingBy(Appointment::getStudentId, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    // Get advisors with most appointments
    public Map<Integer, Long> getAdvisorsWithMostAppointments() {
        List<Appointment> allAppointments = appointmentRepository.findAll();
        return allAppointments.stream()
                .collect(Collectors.groupingBy(Appointment::getAdvisorId, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    // Get appointment counts by status
    public Map<String, Long> getAppointmentCountsByStatus() {
        List<Appointment> allAppointments = appointmentRepository.findAll();
        return allAppointments.stream()
                .collect(Collectors.groupingBy(a -> a.getStatus().name(), Collectors.counting()));
    }

    // Get total counts for dashboard
    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalStudents", (long) studentRepository.findAll().size());
        stats.put("totalFaculty", (long) facultyRepository.findAll().size());
        stats.put("totalAppointments", (long) appointmentRepository.findAll().size());
        stats.put("totalAdvisors", (long) advisorRepository.findByActiveTrue().size());
        return stats;
    }
}
