package com.example.quiz.repository;

import com.example.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByPublishedTrue();
    List<Quiz> findByCategoryIgnoreCase(String category);
    List<Quiz> findByPublishedTrueAndCategoryIgnoreCase(String category);
}
