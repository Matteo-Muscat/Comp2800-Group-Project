package com.myadvice.myadvice.controller;

import com.myadvice.myadvice.entity.User;
import com.myadvice.myadvice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request.email() == null || request.password() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Email and password are required."));
        }

        User user = authService.login(request.email(), request.password());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid credentials or account not approved."));
        }

        return ResponseEntity.ok(ApiDtoFactory.toUserDto(user));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (request.name() == null || request.email() == null || request.password() == null || request.role() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Name, email, password, and role are required."));
        }

        User user = authService.register(request.name(), request.email(), request.password(), request.role());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiDtoFactory.toUserDto(user));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Integer userId) {
        User user = authService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("User not found."));
        }
        return ResponseEntity.ok(ApiDtoFactory.toUserDto(user));
    }

    public record LoginRequest(String email, String password) {}
    public record RegisterRequest(String name, String email, String password, User.Role role) {}
    public record ErrorResponse(String error) {}
}
