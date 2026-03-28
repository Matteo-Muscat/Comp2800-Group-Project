package com.myadvice.myadvice.repository;

import com.myadvice.myadvice.entity.Advisor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdvisorRepository extends JpaRepository<Advisor, Integer> {
    List<Advisor> findByCategoryId(Integer categoryId);
    List<Advisor> findByActiveTrue();
    List<Advisor> findByUserId(Integer userId);
}
