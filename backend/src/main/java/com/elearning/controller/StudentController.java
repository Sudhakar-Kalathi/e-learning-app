package com.elearning.controller;

import com.elearning.model.*;
import com.elearning.repository.*;
import com.elearning.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    LessonRepository lessonRepository;

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    SubmissionRepository submissionRepository;

    private User getLoggedInStudent() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(userDetails.getId()).orElse(null);
    }

    @GetMapping("/courses")
    public ResponseEntity<?> getAllCourses() {
        return ResponseEntity.ok(courseRepository.findAll());
    }

    @GetMapping("/enrollments")
    public ResponseEntity<?> getMyEnrollments() {
        User student = getLoggedInStudent();
        List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);
        return ResponseEntity.ok(enrollments);
    }

    @PostMapping("/courses/{courseId}/enroll")
    public ResponseEntity<?> enrollInCourse(@PathVariable Long courseId) {
        User student = getLoggedInStudent();
        Optional<Course> courseOpt = courseRepository.findById(courseId);

        if(courseOpt.isEmpty()) return ResponseEntity.badRequest().body("Course not found");

        Course course = courseOpt.get();

        if(enrollmentRepository.findByStudentAndCourse(student, course).isPresent()) {
            return ResponseEntity.badRequest().body("Already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment(student, course);
        enrollmentRepository.save(enrollment);

        return ResponseEntity.ok(enrollment);
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<?> getCourseDetails(@PathVariable Long courseId) {
        User student = getLoggedInStudent();
        Optional<Course> courseOpt = courseRepository.findById(courseId);

        if(courseOpt.isEmpty()) return ResponseEntity.badRequest().body("Course not found");
        Course course = courseOpt.get();

        if(enrollmentRepository.findByStudentAndCourse(student, course).isEmpty()) {
            return ResponseEntity.status(403).body("Not enrolled in this course");
        }

        List<Lesson> lessons = lessonRepository.findByCourse(course);
        List<Assignment> assignments = assignmentRepository.findByCourse(course);

        Map<String, Object> details = new HashMap<>();
        details.put("course", course);
        details.put("lessons", lessons);
        details.put("assignments", assignments);

        return ResponseEntity.ok(details);
    }

    @PostMapping("/assignments/{assignmentId}/submit")
    public ResponseEntity<?> submitAssignment(@PathVariable Long assignmentId, @RequestBody Map<String, String> payload) {
        User student = getLoggedInStudent();
        Optional<Assignment> assignmentOpt = assignmentRepository.findById(assignmentId);

        if(assignmentOpt.isEmpty()) return ResponseEntity.badRequest().body("Assignment not found");
        Assignment assignment = assignmentOpt.get();

        Course course = assignment.getCourse();
        if(enrollmentRepository.findByStudentAndCourse(student, course).isEmpty()) {
             return ResponseEntity.status(403).body("Not enrolled in this course");
        }

        String fileUrl = payload.get("fileUrl");

        Optional<Submission> existing = submissionRepository.findByAssignmentAndStudent(assignment, student);
        Submission submission;
        if(existing.isPresent()) {
            submission = existing.get();
            submission.setFileUrl(fileUrl);
        } else {
            submission = new Submission(assignment, student, fileUrl);
        }

        submissionRepository.save(submission);

        // Update progress arbitrarily based on assignment submission
        Enrollment enrollment = enrollmentRepository.findByStudentAndCourse(student, course).get();
        int newProgress = enrollment.getProgressPercentage() + 10;
        enrollment.setProgressPercentage(Math.min(newProgress, 100));
        enrollmentRepository.save(enrollment);

        return ResponseEntity.ok(submission);
    }
}
