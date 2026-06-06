package com.krs.cpanalytics.repository;

import com.krs.cpanalytics.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProblemRepository
        extends JpaRepository<Problem, Long> {

    Optional<Problem> findByContestIdAndProblemIndex(
            Integer contestId,
            String problemIndex
    );
}