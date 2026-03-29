package com.elearning.controller;

import com.elearning.model.*;
import com.elearning.repository.*;
import com.elearning.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/instructor")
@PreAuthorize("hasRole('INSTRUCTOR')")
public class InstructorController {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LessonRepository lessonRepository;

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    SubmissionRepository submissionRepository;

    private User getLoggedInInstructor() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(userDetails.getId()).orElse(null);
    }

    @GetMapping("/courses")
    public ResponseEntity<?> getInstructorCourses() {
        User instructor = getLoggedInInstructor();
        List<Course> courses = courseRepository.findByInstructor(instructor);
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/courses")
    public ResponseEntity<?> createCourse(@RequestBody Course courseRequest) {
        User instructor = getLoggedInInstructor();
        Course course = new Course(courseRequest.getTitle(), courseRequest.getDescription(), instructor);
        courseRepository.save(course);
        return ResponseEntity.ok(course);
    }

    @PostMapping("/courses/{courseId}/lessons")
    public ResponseEntity<?> addLesson(@PathVariable Long courseId, @RequestBody Lesson lessonRequest) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if(courseOpt.isEmpty()) return ResponseEntity.badRequest().body("Course not found");

        Course course = courseOpt.get();
        // Verify ownership
        if(!course.getInstructor().getId().equals(getLoggedInInstructor().getId())) {
            return ResponseEntity.status(403).body("Not aurhorized to modify this course");
        }

        Lesson lesson = new Lesson(lessonRequest.getTitle(), lessonRequest.getContentUrl(), course);
        lessonRepository.save(lesson);
        return ResponseEntity.ok(lesson);
    }

    @PostMapping("/courses/{courseId}/assignments")
    public ResponseEntity<?> addAssignment(@PathVariable Long courseId, @RequestBody Assignment assignmentRequest) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if(courseOpt.isEmpty()) return ResponseEntity.badRequest().body("Course not found");

        Course course = courseOpt.get();
        if(!course.getInstructor().getId().equals(getLoggedInInstructor().getId())) {
            return ResponseEntity.status(403).body("Not authorized");
        }

        // Ensure dueDate is not null (assuming simple string mapping or parsed, here we just use what was sent or set today)
        LocalDate dueDate = assignmentRequest.getDueDate() != null ? assignmentRequest.getDueDate() : LocalDate.now().plusDays(7);

        Assignment assignment = new Assignment(assignmentRequest.getTitle(), assignmentRequest.getDescription(), dueDate, course);
        assignmentRepository.save(assignment);
        return ResponseEntity.ok(assignment);
    }

    @GetMapping("/assignments/{assignmentId}/submissions")
    public ResponseEntity<?> getSubmissions(@PathVariable Long assignmentId) {
        Optional<Assignment> assignmentOpt = assignmentRepository.findById(assignmentId);
        if(assignmentOpt.isEmpty()) return ResponseEntity.badRequest().body("Assignment not found");

        Assignment assignment = assignmentOpt.get();
        if(!assignment.getCourse().getInstructor().getId().equals(getLoggedInInstructor().getId())) {
             return ResponseEntity.status(403).body("Not authorized");
        }

        List<Submission> submissions = submissionRepository.findByAssignment(assignment);
        return ResponseEntity.ok(submissions);
    }

    @PutMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<?> gradeSubmission(@PathVariable Long submissionId, @RequestBody Map<String, Integer> payload) {
        Optional<Submission> submissionOpt = submissionRepository.findById(submissionId);
        if(submissionOpt.isEmpty()) return ResponseEntity.badRequest().body("Submission not found");

        Submission submission = submissionOpt.get();
        if(!submission.getAssignment().getCourse().getInstructor().getId().equals(getLoggedInInstructor().getId())) {
             return ResponseEntity.status(403).body("Not authorized");
        }

        Integer grade = payload.get("grade");
        submission.setGrade(grade);
        submissionRepository.save(submission);
        return ResponseEntity.ok(submission);
    }
}
