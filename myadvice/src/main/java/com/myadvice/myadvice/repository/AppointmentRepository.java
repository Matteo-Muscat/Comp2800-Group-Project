package com.myadvice.myadvice.repository;

import com.myadvice.myadvice.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByStudentId(Integer studentId);
    List<Appointment> findByAdvisorId(Integer advisorId);
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);
    List<Appointment> findByStudentIdAndStatus(Integer studentId, Appointment.AppointmentStatus status);
    List<Appointment> findByAdvisorIdAndStatus(Integer advisorId, Appointment.AppointmentStatus status);
}
