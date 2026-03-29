package com.elearning.repository;

import com.elearning.model.Submission;
import com.elearning.model.Assignment;
import com.elearning.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByAssignment(Assignment assignment);
    List<Submission> findByStudent(User student);
    Optional<Submission> findByAssignmentAndStudent(Assignment assignment, User student);
}
