package com.example.ByteForge.problems;

import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.example.ByteForge.config.Constants.PROBLEMS_PER_PAGE;

@Service
@Slf4j
public class ProblemsService {
    @Autowired
    private ProblemsRepository problemsRepository;

    List<ProblemEntity> searchProblemByKeyword(String keyword, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, PROBLEMS_PER_PAGE);
        return problemsRepository.searchProblemsByKeyword(keyword, pageable);
    }

    Optional<ProblemEntity> findProblemById(Long id) {
        return problemsRepository.findById(id);
    }
}
