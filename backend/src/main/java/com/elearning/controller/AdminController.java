package com.elearning.controller;

import com.elearning.model.Role;
import com.elearning.model.User;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.EnrollmentRepository;
import com.elearning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @GetMapping("/stats")
    public ResponseEntity<?> getPlatformStats() {
        long totalUsers = userRepository.count();
        long totalInstructors = userRepository.findByRole(Role.INSTRUCTOR).size();
        long totalStudents = userRepository.findByRole(Role.STUDENT).size();
        long totalCourses = courseRepository.count();
        long totalEnrollments = enrollmentRepository.count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalInstructors", totalInstructors);
        stats.put("totalStudents", totalStudents);
        stats.put("totalCourses", totalCourses);
        stats.put("totalEnrollments", totalEnrollments);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        // remove passwords before sending
        users.forEach(u -> u.setPassword(null));
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("User not found: " + id);
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
