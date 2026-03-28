package com.myadvice.myadvice.repository;

import com.myadvice.myadvice.entity.StudentCompletedCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentCompletedCourseRepository extends JpaRepository<StudentCompletedCourse, StudentCompletedCourse.StudentCourseId> {
    List<StudentCompletedCourse> findByStudentId(Integer studentId);
    List<StudentCompletedCourse> findByCourseCode(String courseCode);
}
