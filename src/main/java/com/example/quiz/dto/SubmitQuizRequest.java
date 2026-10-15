package com.example.quiz.dto;

import java.util.HashMap;
import java.util.Map;

public class SubmitQuizRequest {

    private Long quizId;
    
    // Map of questionId -> selectedOptionId
    private Map<Long, Long> answers = new HashMap<>();

    public SubmitQuizRequest() {
    }

    public SubmitQuizRequest(Long quizId, Map<Long, Long> answers) {
        this.quizId = quizId;
        this.answers = answers;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Map<Long, Long> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, Long> answers) {
        this.answers = answers;
    }
}
