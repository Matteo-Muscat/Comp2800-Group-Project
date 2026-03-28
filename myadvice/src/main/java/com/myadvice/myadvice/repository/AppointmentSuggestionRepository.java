package com.myadvice.myadvice.repository;

import com.myadvice.myadvice.entity.AppointmentSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentSuggestionRepository extends JpaRepository<AppointmentSuggestion, Integer> {
    List<AppointmentSuggestion> findByAppointmentId(Integer appointmentId);
}
