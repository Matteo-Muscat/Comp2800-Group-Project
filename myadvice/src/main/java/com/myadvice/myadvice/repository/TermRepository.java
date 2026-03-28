package com.myadvice.myadvice.repository;

import com.myadvice.myadvice.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Integer> {
    Optional<Term> findByTermName(String termName);
}
