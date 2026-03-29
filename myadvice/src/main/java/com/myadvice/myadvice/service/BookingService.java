package com.myadvice.myadvice.service;

import com.myadvice.myadvice.entity.*;
import com.myadvice.myadvice.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class BookingService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentSuggestionRepository suggestionRepository;
    private final AdvisorRepository advisorRepository;
    private final AdvisingCategoryRepository categoryRepository;

    public BookingService(AppointmentRepository appointmentRepository,
                          AppointmentSuggestionRepository suggestionRepository,
                          AdvisorRepository advisorRepository,
                          AdvisingCategoryRepository categoryRepository) {
        this.appointmentRepository = appointmentRepository;
        this.suggestionRepository = suggestionRepository;
        this.advisorRepository = advisorRepository;
        this.categoryRepository = categoryRepository;
    }

    // Get all advising categories (shown in dropdown)
    public List<AdvisingCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Get advisors by category
    public List<Advisor> getAdvisorsByCategory(Integer categoryId) {
        return advisorRepository.findByCategoryId(categoryId);
    }

    // Get all active advisors
    public List<Advisor> getAllActiveAdvisors() {
        return advisorRepository.findByActiveTrue();
    }

    // Book an appointment
    public Appointment bookAppointment(Integer studentId, Integer advisorId,
                                        LocalDate date, LocalTime startTime,
                                        LocalTime endTime, String reason) {
        Appointment appointment = new Appointment();
        appointment.setStudentId(studentId);
        appointment.setAdvisorId(advisorId);
        appointment.setRequestedDate(date);
        appointment.setRequestedStartTime(startTime);
        appointment.setRequestedEndTime(endTime);
        appointment.setReason(reason);
        appointment.setStatus(Appointment.AppointmentStatus.REQUESTED);
        return appointmentRepository.save(appointment);
    }

    // Cancel an appointment
    public Appointment cancelAppointment(Integer appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment != null) {
            appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
            return appointmentRepository.save(appointment);
        }
        return null;
    }

    // Approve an appointment (faculty/staff action)
    public Appointment approveAppointment(Integer appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment != null) {
            appointment.setStatus(Appointment.AppointmentStatus.APPROVED);
            return appointmentRepository.save(appointment);
        }
        return null;
    }

    // Deny an appointment (faculty/staff action)
    public Appointment denyAppointment(Integer appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment != null) {
            appointment.setStatus(Appointment.AppointmentStatus.DENIED);
            return appointmentRepository.save(appointment);
        }
        return null;
    }

    // Suggest alternative time for denied appointment
    public AppointmentSuggestion suggestAlternative(Integer appointmentId,
                                                      LocalDate suggestedDate,
                                                      LocalTime startTime,
                                                      LocalTime endTime,
                                                      String message) {
        AppointmentSuggestion suggestion = new AppointmentSuggestion();
        suggestion.setAppointmentId(appointmentId);
        suggestion.setSuggestedDate(suggestedDate);
        suggestion.setSuggestedStartTime(startTime);
        suggestion.setSuggestedEndTime(endTime);
        suggestion.setMessage(message);
        return suggestionRepository.save(suggestion);
    }

    // Get appointments for a student
    public List<Appointment> getStudentAppointments(Integer studentId) {
        return appointmentRepository.findByStudentId(studentId);
    }

    // Get appointments for an advisor
    public List<Appointment> getAdvisorAppointments(Integer advisorId) {
        return appointmentRepository.findByAdvisorId(advisorId);
    }

    // Get pending appointments for an advisor
    public List<Appointment> getPendingAppointments(Integer advisorId) {
        return appointmentRepository.findByAdvisorIdAndStatus(advisorId, Appointment.AppointmentStatus.REQUESTED);
    }

    // Get suggestions for an appointment
    public List<AppointmentSuggestion> getSuggestions(Integer appointmentId) {
        return suggestionRepository.findByAppointmentId(appointmentId);
    }

    // Get all appointments (for reports)
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
}
