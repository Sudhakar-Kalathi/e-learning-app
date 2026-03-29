package com.elearning.model;

import jakarta.persistence.*;

@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    private String fileUrl; // URL or path to the submitted file

    private Integer grade; // Grade out of 100, null if not graded yet

    public Submission() {}

    public Submission(Assignment assignment, User student, String fileUrl) {
        this.assignment = assignment;
        this.student = student;
        this.fileUrl = fileUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Assignment getAssignment() { return assignment; }
    public void setAssignment(Assignment assignment) { this.assignment = assignment; }
    
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    
    public Integer getGrade() { return grade; }
    public void setGrade(Integer grade) { this.grade = grade; }
}
