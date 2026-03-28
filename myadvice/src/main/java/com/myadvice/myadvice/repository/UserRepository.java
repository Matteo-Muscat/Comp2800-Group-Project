package com.myadvice.myadvice.repository;

import com.myadvice.myadvice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmailAndPassword(String email, String password);
    List<User> findByRole(User.Role role);
    List<User> findByStatus(User.Status status);
}
