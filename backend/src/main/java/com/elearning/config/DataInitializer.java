package com.elearning.config;

import com.elearning.model.*;
import com.elearning.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User admin = new User("Demo Admin", "admin@learnsphere.demo",
                passwordEncoder.encode("Demo123!"), Role.ADMIN);
        User instructor = new User("Demo Instructor", "instructor@learnsphere.demo",
                passwordEncoder.encode("Demo123!"), Role.INSTRUCTOR);
        User student = new User("Demo Student", "student@learnsphere.demo",
                passwordEncoder.encode("Demo123!"), Role.STUDENT);

        userRepository.save(admin);
        userRepository.save(instructor);
        userRepository.save(student);

        Course javaCourse = new Course(
                "Java Fundamentals",
                "Build a solid foundation in Java: syntax, OOP, collections, and exception handling.",
                instructor);
        Course frontendCourse = new Course(
                "Modern Frontend Development",
                "Responsive layouts, CSS architecture, and modern JavaScript for interactive UIs.",
                instructor);
        Course springCourse = new Course(
                "Spring Boot Basics",
                "REST APIs, JPA, security, and deployment with Spring Boot.",
                instructor);

        courseRepository.save(javaCourse);
        courseRepository.save(frontendCourse);
        courseRepository.save(springCourse);

        lessonRepository.save(new Lesson("Introduction & Environment", "https://example.com/lesson/java-1", javaCourse));
        lessonRepository.save(new Lesson("OOP in Java", "https://example.com/lesson/java-2", javaCourse));
        lessonRepository.save(new Lesson("Collections & Streams", "https://example.com/lesson/java-3", javaCourse));

        lessonRepository.save(new Lesson("HTML & Semantic Layout", "https://example.com/lesson/fe-1", frontendCourse));
        lessonRepository.save(new Lesson("CSS Grid & Flexbox", "https://example.com/lesson/fe-2", frontendCourse));

        lessonRepository.save(new Lesson("Spring Boot Setup", "https://example.com/lesson/sp-1", springCourse));
        lessonRepository.save(new Lesson("REST Controllers & JPA", "https://example.com/lesson/sp-2", springCourse));

        assignmentRepository.save(new Assignment(
                "Build a CLI Tool",
                "Implement a small Java console app that reads input and applies OOP patterns.",
                LocalDate.now().plusWeeks(2),
                javaCourse));
        assignmentRepository.save(new Assignment(
                "Responsive Landing Page",
                "Create a responsive page using CSS Grid and semantic HTML.",
                LocalDate.now().plusWeeks(1),
                frontendCourse));
        assignmentRepository.save(new Assignment(
                "REST API Mini Project",
                "Expose CRUD endpoints with Spring Data JPA and validation.",
                LocalDate.now().plusWeeks(3),
                springCourse));
    }
}
