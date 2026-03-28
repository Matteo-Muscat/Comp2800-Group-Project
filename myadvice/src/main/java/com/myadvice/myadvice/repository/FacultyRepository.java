package com.myadvice.myadvice.repository;

import com.myadvice.myadvice.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Integer> {
    List<Faculty> findByDepartment(String department);
}
