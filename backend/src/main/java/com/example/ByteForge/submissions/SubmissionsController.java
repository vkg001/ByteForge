package com.example.ByteForge.submissions;

import com.example.ByteForge.submissions.entities.SubmissionDto;
import jakarta.websocket.server.PathParam;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/submissions/")
public class SubmissionsController {

    @GetMapping("/{problem_id}/submissions")
    List<SubmissionDto> findSubmissionsByProblemAndUserId(@PathVariable Long id) {
        throw new RuntimeException("Unimplemented");
    }
}
