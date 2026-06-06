package com.krs.cpanalytics.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.krs.cpanalytics.entity.Problem;
import com.krs.cpanalytics.entity.Submission;
import com.krs.cpanalytics.entity.User;
import com.krs.cpanalytics.repository.ProblemRepository;
import com.krs.cpanalytics.repository.SubmissionRepository;
import com.krs.cpanalytics.repository.UserRepository;
import com.krs.cpanalytics.service.CodeforcesService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sync-submissions")
public class SubmissionSyncController {

    private final CodeforcesService codeforcesService;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;

    public SubmissionSyncController(
            CodeforcesService codeforcesService,
            UserRepository userRepository,
            ProblemRepository problemRepository,
            SubmissionRepository submissionRepository
    ) {
        this.codeforcesService = codeforcesService;
        this.userRepository = userRepository;
        this.problemRepository = problemRepository;
        this.submissionRepository = submissionRepository;
    }

    @GetMapping("/{handle}")
    public String syncSubmissions(
            @PathVariable String handle
    ) throws Exception {

        User user =
                userRepository.findByHandle(handle)
                        .orElseThrow();

        JsonNode root =
                codeforcesService
                        .getUserSubmissions(handle);

        JsonNode submissions =
                root.get("result");

        int limit =
                Math.min(200, submissions.size());

        for (int i = 0; i < limit; i++) {

            JsonNode current =
                    submissions.get(i);

            JsonNode problemNode =
                    current.get("problem");

            Integer contestId =
                    problemNode.has("contestId")
                            ? problemNode
                            .get("contestId")
                            .asInt()
                            : null;

            String index =
                    problemNode.has("index")
                            ? problemNode
                            .get("index")
                            .asText()
                            : null;

            if (contestId == null || index == null)
                continue;

            Problem problem =
                    problemRepository
                            .findByContestIdAndProblemIndex(
                                    contestId,
                                    index
                            )
                            .orElse(new Problem());

            problem.setContestId(contestId);
            problem.setProblemIndex(index);

            if (problemNode.has("name"))
                problem.setName(
                        problemNode.get("name").asText()
                );

            if (problemNode.has("rating"))
                problem.setRating(
                        problemNode.get("rating").asInt()
                );

            if (problemNode.has("tags"))
                problem.setTags(
                        problemNode.get("tags").toString()
                );

            problem =
                    problemRepository.save(problem);

            Long cfSubmissionId =
                    current.get("id").asLong();

            if (submissionRepository
                    .findByCodeforcesSubmissionId(
                            cfSubmissionId
                    )
                    .isPresent()) {

                continue;
            }

            Submission submission =
                    new Submission();

            submission.setCodeforcesSubmissionId(
                    cfSubmissionId
            );

            submission.setUserId(
                    user.getId()
            );

            submission.setProblemId(
                    problem.getId()
            );

            submission.setVerdict(
                    current.get("verdict").asText()
            );

            submission.setSubmissionTime(
                    current.get("creationTimeSeconds")
                            .asLong()
            );

            submissionRepository.save(
                    submission
            );
        }

        return "Synced " + limit + " submissions";
    }
}