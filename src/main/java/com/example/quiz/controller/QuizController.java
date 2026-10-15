package com.example.quiz.controller;

import com.example.quiz.dto.QuizDto;
import com.example.quiz.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class QuizController {

    @Autowired
    private QuizService quizService;

    // Public / Student endpoint: get published quizzes
    @GetMapping
    public ResponseEntity<List<QuizDto>> getAllPublishedQuizzes(@RequestParam(required = false) String category) {
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(quizService.getQuizzesByCategory(category, true));
        }
        return ResponseEntity.ok(quizService.getAllQuizzes(true));
    }

    // Admin endpoint: get all quizzes including unpublished drafts
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<QuizDto>> getAllQuizzesAdmin(@RequestParam(required = false) String category) {
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(quizService.getQuizzesByCategory(category, false));
        }
        return ResponseEntity.ok(quizService.getAllQuizzes(false));
    }

    // Student take quiz: answers are hidden to prevent cheating
    @GetMapping("/{id}")
    public ResponseEntity<QuizDto> getQuizForTaking(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizById(id, true));
    }

    // Admin view quiz: includes correct answers and explanations
    @GetMapping("/{id}/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<QuizDto> getQuizForAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizById(id, false));
    }

    // Admin create new quiz
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<QuizDto> createQuiz(@RequestBody QuizDto quizDto) {
        QuizDto created = quizService.createQuiz(quizDto);
        return ResponseEntity.ok(created);
    }

    // Admin update quiz
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<QuizDto> updateQuiz(@PathVariable Long id, @RequestBody QuizDto quizDto) {
        QuizDto updated = quizService.updateQuiz(id, quizDto);
        return ResponseEntity.ok(updated);
    }

    // Admin delete quiz
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.ok(Map.of("message", "Quiz deleted successfully", "id", id));
    }

    // Admin toggle publish status
    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> togglePublish(@PathVariable Long id, @RequestParam boolean published) {
        QuizDto quiz = quizService.getQuizById(id, false);
        quiz.setPublished(published);
        QuizDto updated = quizService.updateQuiz(id, quiz);
        return ResponseEntity.ok(updated);
    }
}
