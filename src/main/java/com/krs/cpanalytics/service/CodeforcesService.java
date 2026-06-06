package com.krs.cpanalytics.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CodeforcesService {

    private final RestTemplate restTemplate =
            new RestTemplate();

    private final ObjectMapper mapper =
            new ObjectMapper();

    public JsonNode getUserInfo(String handle)
            throws Exception {

        String url =
                "https://codeforces.com/api/user.info?handles="
                        + handle;

        String response =
                restTemplate.getForObject(
                        url,
                        String.class
                );

        return mapper.readTree(response);
    }

    public JsonNode getUserSubmissions(
            String handle
    ) throws Exception {

        String url =
                "https://codeforces.com/api/user.status?handle="
                        + handle;

        String response =
                restTemplate.getForObject(
                        url,
                        String.class
                );

        return mapper.readTree(response);
    }
}