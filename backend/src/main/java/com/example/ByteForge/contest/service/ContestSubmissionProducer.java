package com.example.ByteForge.contest.service;

import com.example.ByteForge.contest.dto.message.RawSubmissionMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContestSubmissionProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendRawSubmission(RawSubmissionMessage message) {
        try {
            String topic = "contest-" + message.getContestId() + "-submissions-raw";
            String payload = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(topic, payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization failed", e);
        }
    }
}