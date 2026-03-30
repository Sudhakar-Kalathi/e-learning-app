package com.elearning.controller;

import com.elearning.model.Assignment;
import com.elearning.model.Course;
import com.elearning.model.Lesson;
import com.elearning.repository.AssignmentRepository;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> listCourses() {
        return ResponseEntity.ok(courseRepository.findAll());
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<?> getCourseDetail(@PathVariable Long courseId) {
        Optional<Course> opt = courseRepository.findById(courseId);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Course course = opt.get();
        List<Lesson> lessons = lessonRepository.findByCourse(course);
        List<Assignment> assignments = assignmentRepository.findByCourse(course);

        Map<String, Object> body = new HashMap<>();
        body.put("course", course);
        body.put("lessons", lessons);
        body.put("assignments", assignments);
        return ResponseEntity.ok(body);
    }
}
