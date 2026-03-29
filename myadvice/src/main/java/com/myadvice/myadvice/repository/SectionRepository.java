package com.myadvice.myadvice.repository;

import com.myadvice.myadvice.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Integer> {
    List<Section> findByCourseCode(String courseCode);
    List<Section> findByTermId(Integer termId);
    List<Section> findByCourseCodeAndTermId(String courseCode, Integer termId);
    List<Section> findByInstructorUserId(Integer instructorUserId);
}
