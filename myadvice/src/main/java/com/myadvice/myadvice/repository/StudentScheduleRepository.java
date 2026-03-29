package com.myadvice.myadvice.repository;

import com.myadvice.myadvice.entity.StudentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentScheduleRepository extends JpaRepository<StudentSchedule, StudentSchedule.ScheduleId> {
    List<StudentSchedule> findByStudentId(Integer studentId);
    List<StudentSchedule> findByStudentIdAndTermId(Integer studentId, Integer termId);
    List<StudentSchedule> findBySectionId(Integer sectionId);
}
