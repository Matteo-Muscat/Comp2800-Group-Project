package com.myadvice.myadvice.repository;

import com.myadvice.myadvice.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, String> {
    List<Course> findByCourseNameContaining(String keyword);
}
