package com.elearning.repository;

import com.elearning.model.Assignment;
import com.elearning.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByCourse(Course course);
}
