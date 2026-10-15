package com.example.quiz.repository;

import com.example.quiz.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByUserIdOrderBySubmittedAtDesc(Long userId);
    List<Result> findByQuizIdOrderBySubmittedAtDesc(Long quizId);
    List<Result> findAllByOrderBySubmittedAtDesc();
}
