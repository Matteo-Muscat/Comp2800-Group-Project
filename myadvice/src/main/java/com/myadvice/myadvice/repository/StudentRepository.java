package com.myadvice.myadvice.repository;

import com.myadvice.myadvice.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    List<Student> findByMajor(String major);
    List<Student> findByProgram(String program);
    List<Student> findByYearLevel(Integer yearLevel);
}
