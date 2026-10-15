package com.example.quiz.controller;

import com.example.quiz.dto.ResultDto;
import com.example.quiz.dto.SubmitQuizRequest;
import com.example.quiz.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/results")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ResultController {

    @Autowired
    private ResultService resultService;

    // Student submits quiz
    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody SubmitQuizRequest request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "User must be authenticated to submit a quiz"));
        }
        ResultDto result = resultService.submitQuiz(request, authentication.getName());
        return ResponseEntity.ok(result);
    }

    // Student views their own past results
    @GetMapping("/student")
    public ResponseEntity<List<ResultDto>> getStudentResults(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(resultService.getResultsByUsername(authentication.getName()));
    }

    // View specific result details (by student or admin)
    @GetMapping("/{id}")
    public ResponseEntity<ResultDto> getResultById(@PathVariable Long id) {
        return ResponseEntity.ok(resultService.getResultById(id));
    }

    // Admin views all student results across system
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ResultDto>> getAllResults() {
        return ResponseEntity.ok(resultService.getAllResults());
    }

    // Admin views results for a specific quiz
    @GetMapping("/quiz/{quizId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ResultDto>> getResultsByQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(resultService.getResultsByQuizId(quizId));
    }
}
