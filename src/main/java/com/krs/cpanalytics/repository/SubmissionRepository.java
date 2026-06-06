package com.krs.cpanalytics.repository;

import com.krs.cpanalytics.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubmissionRepository
        extends JpaRepository<Submission, Long> {

    Optional<Submission>
    findByCodeforcesSubmissionId(
            Long codeforcesSubmissionId
    );
}