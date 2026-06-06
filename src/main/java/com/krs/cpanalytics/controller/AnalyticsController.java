package com.krs.cpanalytics.controller;

import com.krs.cpanalytics.entity.Problem;
import com.krs.cpanalytics.entity.Submission;
import com.krs.cpanalytics.entity.User;
import com.krs.cpanalytics.repository.ProblemRepository;
import com.krs.cpanalytics.repository.SubmissionRepository;
import com.krs.cpanalytics.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final ProblemRepository problemRepository;

    public AnalyticsController(
            UserRepository userRepository,
            SubmissionRepository submissionRepository,
            ProblemRepository problemRepository
    ) {
        this.userRepository = userRepository;
        this.submissionRepository = submissionRepository;
        this.problemRepository = problemRepository;
    }

    @GetMapping("/{handle}/topics")
    public Map<String, Integer> topicStats(
            @PathVariable String handle
    ) {

        User user =
                userRepository.findByHandle(handle)
                        .orElseThrow();

        List<Submission> submissions =
                submissionRepository.findAll();

        Map<String, Integer> result =
                new HashMap<>();

        for (Submission submission : submissions) {

            if (!submission.getUserId()
                    .equals(user.getId()))
                continue;

            Problem problem =
                    problemRepository.findById(
                                    submission.getProblemId())
                            .orElse(null);

            if (problem == null)
                continue;

            String tags =
                    problem.getTags();

            if (tags == null)
                continue;

            String cleaned =
                    tags.replace("[", "")
                            .replace("]", "")
                            .replace("\"", "");

            String[] tagArray =
                    cleaned.split(",");

            for (String tag : tagArray) {

                tag = tag.trim();

                if (tag.isEmpty())
                    continue;

                result.put(
                        tag,
                        result.getOrDefault(tag, 0) + 1
                );
            }
        }

        return result;
    }

    @GetMapping("/{handle}/weak-topics")
    public List<String> weakTopics(
            @PathVariable String handle
    ) {

        Map<String, Integer> topics =
                topicStats(handle);

        List<Map.Entry<String, Integer>> list =
                new ArrayList<>(topics.entrySet());

        list.sort(
                Comparator.comparingInt(
                        Map.Entry::getValue
                )
        );

        List<String> result =
                new ArrayList<>();

        int limit =
                Math.min(5, list.size());

        for (int i = 0; i < limit; i++) {

            result.add(
                    list.get(i).getKey()
            );
        }

        return result;
    }

    @GetMapping("/{handle}/rating-distribution")
    public Map<String, Integer> ratingDistribution(
            @PathVariable String handle
    ) {

        User user =
                userRepository.findByHandle(handle)
                        .orElseThrow();

        List<Submission> submissions =
                submissionRepository.findAll();

        Map<String, Integer> result =
                new HashMap<>();

        result.put("800-1199", 0);
        result.put("1200-1599", 0);
        result.put("1600-1999", 0);
        result.put("2000-2399", 0);
        result.put("2400+", 0);

        for (Submission submission : submissions) {

            if (!submission.getUserId()
                    .equals(user.getId()))
                continue;

            Problem problem =
                    problemRepository.findById(
                            submission.getProblemId()
                    ).orElse(null);

            if (problem == null)
                continue;

            Integer rating =
                    problem.getRating();

            if (rating == null)
                continue;

            if (rating < 1200)
                result.put(
                        "800-1199",
                        result.get("800-1199") + 1
                );

            else if (rating < 1600)
                result.put(
                        "1200-1599",
                        result.get("1200-1599") + 1
                );

            else if (rating < 2000)
                result.put(
                        "1600-1999",
                        result.get("1600-1999") + 1
                );

            else if (rating < 2400)
                result.put(
                        "2000-2399",
                        result.get("2000-2399") + 1
                );

            else
                result.put(
                        "2400+",
                        result.get("2400+") + 1
                );
        }

        return result;
    }

    @GetMapping("/{handle}/solved-topics")
    public Map<String, Integer> solvedTopics(
            @PathVariable String handle
    ) {

        User user =
                userRepository.findByHandle(handle)
                        .orElseThrow();

        List<Submission> submissions =
                submissionRepository.findAll();

        Map<String, Integer> result =
                new HashMap<>();

        Set<Long> solvedProblems =
                new HashSet<>();

        for (Submission submission : submissions) {

            if (!submission.getUserId()
                    .equals(user.getId()))
                continue;

            if (!"OK".equals(
                    submission.getVerdict()))
                continue;

            solvedProblems.add(
                    submission.getProblemId()
            );
        }

        for (Long problemId : solvedProblems) {

            Problem problem =
                    problemRepository.findById(problemId)
                            .orElse(null);

            if (problem == null)
                continue;

            String tags =
                    problem.getTags();

            if (tags == null)
                continue;

            String cleaned =
                    tags.replace("[", "")
                            .replace("]", "")
                            .replace("\"", "");

            String[] tagArray =
                    cleaned.split(",");

            for (String tag : tagArray) {

                tag = tag.trim();

                if (tag.isEmpty())
                    continue;

                result.put(
                        tag,
                        result.getOrDefault(tag, 0) + 1
                );
            }
        }

        return result;
    }
}