package com.example.quiz.dto;

import java.util.ArrayList;
import java.util.List;

public class QuestionDto {

    private Long id;
    private Long quizId;
    private String questionText;
    private String explanation;
    private List<OptionDto> options = new ArrayList<>();

    public QuestionDto() {
    }

    public QuestionDto(Long id, Long quizId, String questionText, String explanation, List<OptionDto> options) {
        this.id = id;
        this.quizId = quizId;
        this.questionText = questionText;
        this.explanation = explanation;
        this.options = options;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }
}
