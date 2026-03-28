package com.myadvice.myadvice.repository;

import com.myadvice.myadvice.entity.SectionMeeting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SectionMeetingRepository extends JpaRepository<SectionMeeting, Integer> {
    List<SectionMeeting> findBySectionId(Integer sectionId);
}
