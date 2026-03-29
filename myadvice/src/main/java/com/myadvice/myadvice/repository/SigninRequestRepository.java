package com.myadvice.myadvice.repository;

import com.myadvice.myadvice.entity.SigninRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SigninRequestRepository extends JpaRepository<SigninRequest, Integer> {
    List<SigninRequest> findByUserId(Integer userId);
    List<SigninRequest> findByDecision(SigninRequest.Decision decision);
}
