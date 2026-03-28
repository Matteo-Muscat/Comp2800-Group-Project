package com.myadvice.myadvice.repository;

import com.myadvice.myadvice.entity.TimetableChange;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TimetableChangeRepository extends JpaRepository<TimetableChange, Integer> {
    List<TimetableChange> findBySectionId(Integer sectionId);
    List<TimetableChange> findByAdminUserId(Integer adminUserId);
}
