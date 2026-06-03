package com.example.ByteForge.submissions;

import com.example.ByteForge.submissions.entities.ProblemSubmissionStatus;
import com.example.ByteForge.submissions.entities.SubmissionDto;
import com.example.ByteForge.submissions.entities.SubmissionEntity;
import com.example.ByteForge.submissions.entities.SubmissionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

@Service
public class SubmissionsService {
    @Autowired
    SubmissionsRepository repository;

    public SubmissionDto findSubmissionByProblemAndUserId(Long problemId, Long userId, Pageable pageable) {
        List<SubmissionEntity> submissions = repository.findSubmissionByProblemAndUserId(problemId, userId, pageable);
        SubmissionDto res = new SubmissionDto();
        res.setAllSubmissions(submissions);

        if (submissions.isEmpty()) {
            res.setStatus(ProblemSubmissionStatus.UAT);
            return res;
        }

        for (var submission: submissions) {
            if (submission.getSubmissionStatus() == SubmissionStatus.ACC) {
                res.setStatus(ProblemSubmissionStatus.ACC);
                return res;
            }
        }

        res.setStatus(ProblemSubmissionStatus.ATT);
        return res;
    }
}
