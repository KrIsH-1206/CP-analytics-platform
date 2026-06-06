package com.krs.cpanalytics.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.krs.cpanalytics.entity.User;
import com.krs.cpanalytics.repository.UserRepository;
import com.krs.cpanalytics.service.CodeforcesService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sync")
public class SyncController {

    private final CodeforcesService codeforcesService;
    private final UserRepository userRepository;

    public SyncController(
            CodeforcesService codeforcesService,
            UserRepository userRepository
    ) {
        this.codeforcesService = codeforcesService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{handle}")
    public User syncUser(
            @PathVariable String handle
    ) throws Exception {

        JsonNode root =
                codeforcesService.getUserInfo(handle);

        JsonNode userNode =
                root.get("result").get(0);

        User user =
                userRepository
                        .findByHandle(handle)
                        .orElse(new User());

        user.setHandle(
                userNode.get("handle").asText()
        );

        user.setCurrentRating(
                userNode.get("rating").asInt()
        );

        user.setMaxRating(
                userNode.get("maxRating").asInt()
        );

        user.setRank(
                userNode.get("rank").asText()
        );

        user.setMaxRank(
                userNode.get("maxRank").asText()
        );

        user.setCountry(
                userNode.has("country")
                        ? userNode.get("country").asText()
                        : null
        );

        user.setOrganization(
                userNode.has("organization")
                        ? userNode.get("organization").asText()
                        : null
        );

        user.setContribution(
                userNode.get("contribution").asInt()
        );

        return userRepository.save(user);
    }
}