package com.example.ByteForge.problems;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.ByteForge.problems.entities.ProblemEntity;
import com.example.ByteForge.problems.entities.TestCaseEntity;
import jakarta.transaction.Transactional;
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

    public List<ProblemEntity> searchProblemByKeyword(String keyword, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, PROBLEMS_PER_PAGE);
        List<ProblemEntity> res = problemsRepository.searchProblemsByKeyword(keyword, pageable);
        for (var problem: res) {
            List<TestCaseEntity> showAbleTestCases = new ArrayList<>();
            for (var test: problem.getTestCases()) {
                if (!test.getHidden()) showAbleTestCases.add(test);
            }

            problem.setTestCases(showAbleTestCases);

            for (var boilerPlate: problem.getBoilerPlateCodes()) {
                boilerPlate.setAppendCode("");
                boilerPlate.setPrependCode("");
            }
        }
        return res;
    }

    public Optional<ProblemEntity> findProblemById(Long id) {
        return problemsRepository.findById(id);
    }

    @Transactional
    void saveProblem(ProblemEntity problem) {
        problemsRepository.save(problem);
    }
}
