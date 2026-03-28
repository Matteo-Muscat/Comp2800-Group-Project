package com.myadvice.myadvice.repository;

import com.myadvice.myadvice.entity.CoursePrerequisite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CoursePrerequisiteRepository extends JpaRepository<CoursePrerequisite, CoursePrerequisite.PrerequisiteId> {
    List<CoursePrerequisite> findByCourseCode(String courseCode);
    List<CoursePrerequisite> findByPrereqCourseCode(String prereqCourseCode);
}
