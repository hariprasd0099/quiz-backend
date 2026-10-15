package com.example.quiz.service;

import com.example.quiz.dto.ResultDto;
import com.example.quiz.dto.SubmitQuizRequest;
import com.example.quiz.entity.*;
import com.example.quiz.repository.QuizRepository;
import com.example.quiz.repository.ResultRepository;
import com.example.quiz.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResultService {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResultDto submitQuiz(SubmitQuizRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + request.getQuizId()));

        List<Question> questions = quiz.getQuestions();
        int totalQuestions = questions.size();
        int correctAnswers = 0;

        List<Map<String, Object>> answerDetails = new ArrayList<>();
        Map<Long, Long> submittedAnswers = request.getAnswers() != null ? request.getAnswers() : Collections.emptyMap();

        for (Question q : questions) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("questionId", q.getId());
            detail.put("questionText", q.getQuestionText());
            detail.put("explanation", q.getExplanation());

            Long selectedOptionId = submittedAnswers.get(q.getId());
            detail.put("selectedOptionId", selectedOptionId);

            Option correctOption = q.getOptions().stream()
                    .filter(Option::isCorrect)
                    .findFirst()
                    .orElse(null);

            Long correctOptionId = (correctOption != null) ? correctOption.getId() : null;
            detail.put("correctOptionId", correctOptionId);
            detail.put("correctOptionText", correctOption != null ? correctOption.getOptionText() : "");

            boolean isQuestionCorrect = selectedOptionId != null && selectedOptionId.equals(correctOptionId);
            detail.put("isCorrect", isQuestionCorrect);

            Option selectedOption = q.getOptions().stream()
                    .filter(opt -> opt.getId().equals(selectedOptionId))
                    .findFirst()
                    .orElse(null);
            detail.put("selectedOptionText", selectedOption != null ? selectedOption.getOptionText() : "Not answered");

            // Include all options for detailed review
            List<Map<String, Object>> optsList = q.getOptions().stream().map(opt -> {
                Map<String, Object> oMap = new HashMap<>();
                oMap.put("id", opt.getId());
                oMap.put("optionText", opt.getOptionText());
                oMap.put("isCorrect", opt.isCorrect());
                return oMap;
            }).collect(Collectors.toList());
            detail.put("options", optsList);

            if (isQuestionCorrect) {
                correctAnswers++;
            }

            answerDetails.add(detail);
        }

        int scorePercentage = totalQuestions > 0 ? (int) Math.round(((double) correctAnswers / totalQuestions) * 100) : 0;
        boolean passed = scorePercentage >= quiz.getPassingScore();

        String answersJson = "";
        try {
            answersJson = objectMapper.writeValueAsString(answerDetails);
        } catch (JsonProcessingException e) {
            answersJson = "[]";
        }

        Result result = new Result(user, quiz, scorePercentage, totalQuestions, correctAnswers, passed, answersJson);
        Result saved = resultRepository.save(result);

        return toResultDto(saved);
    }

    public List<ResultDto> getResultsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return resultRepository.findByUserIdOrderBySubmittedAtDesc(user.getId()).stream()
                .map(this::toResultDto)
                .collect(Collectors.toList());
    }

    public List<ResultDto> getAllResults() {
        return resultRepository.findAllByOrderBySubmittedAtDesc().stream()
                .map(this::toResultDto)
                .collect(Collectors.toList());
    }

    public List<ResultDto> getResultsByQuizId(Long quizId) {
        return resultRepository.findByQuizIdOrderBySubmittedAtDesc(quizId).stream()
                .map(this::toResultDto)
                .collect(Collectors.toList());
    }

    public ResultDto getResultById(Long id) {
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Result not found with id: " + id));
        return toResultDto(result);
    }

    private ResultDto toResultDto(Result result) {
        ResultDto dto = new ResultDto();
        dto.setId(result.getId());
        dto.setUserId(result.getUser().getId());
        dto.setUsername(result.getUser().getUsername());
        dto.setStudentName(result.getUser().getFullName() != null ? result.getUser().getFullName() : result.getUser().getUsername());
        dto.setQuizId(result.getQuiz().getId());
        dto.setQuizTitle(result.getQuiz().getTitle());
        dto.setQuizCategory(result.getQuiz().getCategory());
        dto.setScore(result.getScore());
        dto.setTotalQuestions(result.getTotalQuestions());
        dto.setCorrectAnswers(result.getCorrectAnswers());
        dto.setPassed(result.isPassed());
        dto.setPassingScore(result.getQuiz().getPassingScore());
        dto.setAnswersDetail(result.getAnswersDetail());
        dto.setSubmittedAt(result.getSubmittedAt());
        return dto;
    }
}
