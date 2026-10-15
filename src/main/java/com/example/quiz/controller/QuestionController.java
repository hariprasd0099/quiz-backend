package com.example.quiz.controller;

import com.example.quiz.dto.QuestionDto;
import com.example.quiz.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class QuestionController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/quiz/{quizId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<QuestionDto>> getQuestionsByQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuestionsByQuizId(quizId, false));
    }

    @PostMapping("/quiz/{quizId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<QuestionDto> addQuestion(@PathVariable Long quizId, @RequestBody QuestionDto questionDto) {
        QuestionDto created = quizService.addQuestionToQuiz(quizId, questionDto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<QuestionDto> updateQuestion(@PathVariable Long id, @RequestBody QuestionDto questionDto) {
        QuestionDto updated = quizService.updateQuestion(id, questionDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        quizService.deleteQuestion(id);
        return ResponseEntity.ok(Map.of("message", "Question deleted successfully", "id", id));
    }
}
