package com.elearning.repository;

import com.elearning.model.Lesson;
import com.elearning.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByCourse(Course course);
}
