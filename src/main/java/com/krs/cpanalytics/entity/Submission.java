package com.krs.cpanalytics.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long problemId;

    private Long codeforcesSubmissionId;

    private String verdict;

    private Long submissionTime;

    public Submission() {
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public Long getCodeforcesSubmissionId() {
        return codeforcesSubmissionId;
    }

    public void setCodeforcesSubmissionId(Long codeforcesSubmissionId) {
        this.codeforcesSubmissionId = codeforcesSubmissionId;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public Long getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(Long submissionTime) {
        this.submissionTime = submissionTime;
    }
}