package com.myadvice.myadvice.controller;

import com.myadvice.myadvice.entity.Appointment;
import com.myadvice.myadvice.entity.AppointmentSuggestion;
import com.myadvice.myadvice.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/categories")
    public List<ApiDtoFactory.AdvisingCategoryDto> getCategories() {
        return bookingService.getAllCategories().stream().map(ApiDtoFactory::toAdvisingCategoryDto).toList();
    }

    @GetMapping("/advisors")
    public List<ApiDtoFactory.AdvisorDto> getAdvisors(@RequestParam(required = false) Integer categoryId) {
        if (categoryId != null) {
            return bookingService.getAdvisorsByCategory(categoryId).stream().map(ApiDtoFactory::toAdvisorDto).toList();
        }
        return bookingService.getAllActiveAdvisors().stream().map(ApiDtoFactory::toAdvisorDto).toList();
    }

    @PostMapping("/appointments")
    public ResponseEntity<ApiDtoFactory.AppointmentDto> bookAppointment(@RequestBody BookAppointmentRequest request) {
        Appointment appointment = bookingService.bookAppointment(
                request.studentId(),
                request.advisorId(),
                request.requestedDate(),
                request.requestedStartTime(),
                request.requestedEndTime(),
                request.reason()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiDtoFactory.toAppointmentDto(appointment));
    }

    @GetMapping("/appointments")
    public List<ApiDtoFactory.AppointmentDto> getAllAppointments() {
        return bookingService.getAllAppointments().stream().map(ApiDtoFactory::toAppointmentDto).toList();
    }

    @GetMapping("/students/{studentId}/appointments")
    public List<ApiDtoFactory.AppointmentDto> getStudentAppointments(@PathVariable Integer studentId) {
        return bookingService.getStudentAppointments(studentId).stream().map(ApiDtoFactory::toAppointmentDto).toList();
    }

    @GetMapping("/advisors/{advisorId}/appointments")
    public List<ApiDtoFactory.AppointmentDto> getAdvisorAppointments(@PathVariable Integer advisorId,
                                                                     @RequestParam(defaultValue = "false") boolean pendingOnly) {
        if (pendingOnly) {
            return bookingService.getPendingAppointments(advisorId).stream().map(ApiDtoFactory::toAppointmentDto).toList();
        }
        return bookingService.getAdvisorAppointments(advisorId).stream().map(ApiDtoFactory::toAppointmentDto).toList();
    }

    @PostMapping("/appointments/{appointmentId}/approve")
    public ResponseEntity<?> approveAppointment(@PathVariable Integer appointmentId) {
        Appointment appointment = bookingService.approveAppointment(appointmentId);
        if (appointment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Appointment not found."));
        }
        return ResponseEntity.ok(ApiDtoFactory.toAppointmentDto(appointment));
    }

    @PostMapping("/appointments/{appointmentId}/deny")
    public ResponseEntity<?> denyAppointment(@PathVariable Integer appointmentId) {
        Appointment appointment = bookingService.denyAppointment(appointmentId);
        if (appointment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Appointment not found."));
        }
        return ResponseEntity.ok(ApiDtoFactory.toAppointmentDto(appointment));
    }

    @PostMapping("/appointments/{appointmentId}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Integer appointmentId) {
        Appointment appointment = bookingService.cancelAppointment(appointmentId);
        if (appointment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Appointment not found."));
        }
        return ResponseEntity.ok(ApiDtoFactory.toAppointmentDto(appointment));
    }

    @PostMapping("/appointments/{appointmentId}/suggestions")
    public ResponseEntity<ApiDtoFactory.AppointmentSuggestionDto> suggestAlternative(@PathVariable Integer appointmentId,
                                                                                     @RequestBody SuggestAlternativeRequest request) {
        AppointmentSuggestion suggestion = bookingService.suggestAlternative(
                appointmentId,
                request.suggestedDate(),
                request.suggestedStartTime(),
                request.suggestedEndTime(),
                request.message()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiDtoFactory.toAppointmentSuggestionDto(suggestion));
    }

    @GetMapping("/appointments/{appointmentId}/suggestions")
    public List<ApiDtoFactory.AppointmentSuggestionDto> getSuggestions(@PathVariable Integer appointmentId) {
        return bookingService.getSuggestions(appointmentId).stream()
                .map(ApiDtoFactory::toAppointmentSuggestionDto)
                .toList();
    }

    public record BookAppointmentRequest(
            Integer studentId,
            Integer advisorId,
            LocalDate requestedDate,
            LocalTime requestedStartTime,
            LocalTime requestedEndTime,
            String reason
    ) {}

    public record SuggestAlternativeRequest(
            LocalDate suggestedDate,
            LocalTime suggestedStartTime,
            LocalTime suggestedEndTime,
            String message
    ) {}

    public record ErrorResponse(String error) {}
}
