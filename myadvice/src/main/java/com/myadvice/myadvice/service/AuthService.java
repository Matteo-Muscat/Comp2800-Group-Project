package com.myadvice.myadvice.service;

import com.myadvice.myadvice.entity.User;
import com.myadvice.myadvice.entity.Student;
import com.myadvice.myadvice.entity.Faculty;
import com.myadvice.myadvice.entity.SigninRequest;
import com.myadvice.myadvice.repository.UserRepository;
import com.myadvice.myadvice.repository.SigninRequestRepository;
import com.myadvice.myadvice.repository.StudentRepository;
import com.myadvice.myadvice.repository.FacultyRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SigninRequestRepository signinRequestRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    public AuthService(UserRepository userRepository,
                       SigninRequestRepository signinRequestRepository,
                       StudentRepository studentRepository,
                       FacultyRepository facultyRepository) {
        this.userRepository = userRepository;
        this.signinRequestRepository = signinRequestRepository;
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    // Login - check email and password
    public User login(String email, String password) {
        Optional<User> user = userRepository.findByEmailAndPassword(email, password);
        if (user.isPresent() && user.get().getStatus() == User.Status.APPROVED) {
            ensureProfileExists(user.get());
            return user.get();
        }
        return null;
    }

    // Register a new user
    public User register(String name, String email, String password, User.Role role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setStatus(User.Status.PENDING);
        User savedUser = userRepository.save(user);
        ensureProfileExists(savedUser);

        // Create a signin request for admin to approve
        SigninRequest request = new SigninRequest();
        request.setUserId(savedUser.getUserId());
        request.setDecision(SigninRequest.Decision.PENDING);
        signinRequestRepository.save(request);

        return savedUser;
    }

    // Get user by ID
    public User getUserById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }

    private void ensureProfileExists(User user) {
        if (user.getRole() == User.Role.STUDENT && !studentRepository.existsById(user.getUserId())) {
            Student student = new Student();
            student.setStudentId(user.getUserId());
            student.setEmail(user.getEmail());
            student.setMajor("Undeclared");
            student.setProgram("Pending Profile Setup");
            student.setYearLevel(1);
            studentRepository.save(student);
        }

        if (user.getRole() == User.Role.FACULTY && !facultyRepository.existsById(user.getUserId())) {
            Faculty faculty = new Faculty();
            faculty.setFacultyId(user.getUserId());
            faculty.setEmail(user.getEmail());
            faculty.setDepartment("TBD");
            faculty.setOfficeLocation("TBD");
            faculty.setResearchInterests("TBD");
            facultyRepository.save(faculty);
        }
    }
}
