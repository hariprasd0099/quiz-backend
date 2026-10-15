package com.example.quiz.service;

import com.example.quiz.dto.OptionDto;
import com.example.quiz.dto.QuestionDto;
import com.example.quiz.dto.QuizDto;
import com.example.quiz.entity.Option;
import com.example.quiz.entity.Question;
import com.example.quiz.entity.Quiz;
import com.example.quiz.repository.QuestionRepository;
import com.example.quiz.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public List<QuizDto> getAllQuizzes(boolean onlyPublished) {
        List<Quiz> quizzes = onlyPublished ? quizRepository.findByPublishedTrue() : quizRepository.findAll();
        return quizzes.stream()
                .map(q -> toQuizDto(q, true)) // true = hide answers from general list
                .collect(Collectors.toList());
    }

    public List<QuizDto> getQuizzesByCategory(String category, boolean onlyPublished) {
        List<Quiz> quizzes = onlyPublished 
                ? quizRepository.findByPublishedTrueAndCategoryIgnoreCase(category)
                : quizRepository.findByCategoryIgnoreCase(category);
        return quizzes.stream()
                .map(q -> toQuizDto(q, true))
                .collect(Collectors.toList());
    }

    public QuizDto getQuizById(Long id, boolean hideAnswers) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
        return toQuizDto(quiz, hideAnswers);
    }

    public Quiz getQuizEntityById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
    }

    public QuizDto createQuiz(QuizDto dto) {
        Quiz quiz = new Quiz(
                dto.getTitle(),
                dto.getDescription(),
                dto.getCategory(),
                dto.getDurationMinutes() > 0 ? dto.getDurationMinutes() : 15,
                dto.getPassingScore() > 0 ? dto.getPassingScore() : 60,
                dto.isPublished()
        );

        if (dto.getQuestions() != null) {
            for (QuestionDto qDto : dto.getQuestions()) {
                Question question = new Question(qDto.getQuestionText(), qDto.getExplanation());
                if (qDto.getOptions() != null) {
                    for (OptionDto optDto : qDto.getOptions()) {
                        Option option = new Option(optDto.getOptionText(), Boolean.TRUE.equals(optDto.getIsCorrect()));
                        question.addOption(option);
                    }
                }
                quiz.addQuestion(question);
            }
        }

        Quiz saved = quizRepository.save(quiz);
        return toQuizDto(saved, false);
    }

    public QuizDto updateQuiz(Long id, QuizDto dto) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));

        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setCategory(dto.getCategory());
        quiz.setDurationMinutes(dto.getDurationMinutes());
        quiz.setPassingScore(dto.getPassingScore());
        quiz.setPublished(dto.isPublished());

        Quiz updated = quizRepository.save(quiz);
        return toQuizDto(updated, false);
    }

    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new RuntimeException("Quiz not found with id: " + id);
        }
        quizRepository.deleteById(id);
    }

    public QuestionDto addQuestionToQuiz(Long quizId, QuestionDto questionDto) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + quizId));

        Question question = new Question(questionDto.getQuestionText(), questionDto.getExplanation());
        if (questionDto.getOptions() != null) {
            for (OptionDto optDto : questionDto.getOptions()) {
                Option option = new Option(optDto.getOptionText(), Boolean.TRUE.equals(optDto.getIsCorrect()));
                question.addOption(option);
            }
        }
        quiz.addQuestion(question);
        quizRepository.save(quiz);

        return toQuestionDto(question, false);
    }

    public QuestionDto updateQuestion(Long questionId, QuestionDto questionDto) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + questionId));

        question.setQuestionText(questionDto.getQuestionText());
        question.setExplanation(questionDto.getExplanation());

        question.getOptions().clear();
        if (questionDto.getOptions() != null) {
            for (OptionDto optDto : questionDto.getOptions()) {
                Option option = new Option(optDto.getOptionText(), Boolean.TRUE.equals(optDto.getIsCorrect()));
                question.addOption(option);
            }
        }

        Question updated = questionRepository.save(question);
        return toQuestionDto(updated, false);
    }

    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + questionId));
        Quiz quiz = question.getQuiz();
        if (quiz != null) {
            quiz.removeQuestion(question);
        }
        questionRepository.delete(question);
    }

    public List<QuestionDto> getQuestionsByQuizId(Long quizId, boolean hideAnswers) {
        List<Question> questions = questionRepository.findByQuizId(quizId);
        return questions.stream()
                .map(q -> toQuestionDto(q, hideAnswers))
                .collect(Collectors.toList());
    }

    public QuizDto toQuizDto(Quiz quiz, boolean hideAnswers) {
        QuizDto dto = new QuizDto();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setCategory(quiz.getCategory());
        dto.setDurationMinutes(quiz.getDurationMinutes());
        dto.setPassingScore(quiz.getPassingScore());
        dto.setPublished(quiz.isPublished());
        dto.setCreatedAt(quiz.getCreatedAt());

        if (quiz.getQuestions() != null) {
            dto.setQuestionCount(quiz.getQuestions().size());
            List<QuestionDto> qDtos = quiz.getQuestions().stream()
                    .map(q -> toQuestionDto(q, hideAnswers))
                    .collect(Collectors.toList());
            dto.setQuestions(qDtos);
        } else {
            dto.setQuestionCount(0);
        }

        return dto;
    }

    public QuestionDto toQuestionDto(Question question, boolean hideAnswers) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setQuizId(question.getQuiz() != null ? question.getQuiz().getId() : null);
        dto.setQuestionText(question.getQuestionText());
        // For students taking test, hide explanation; reveal only to admin or on result page
        dto.setExplanation(hideAnswers ? null : question.getExplanation());

        if (question.getOptions() != null) {
            List<OptionDto> optDtos = question.getOptions().stream().map(opt -> {
                OptionDto optDto = new OptionDto();
                optDto.setId(opt.getId());
                optDto.setOptionText(opt.getOptionText());
                // Crucial security: hide isCorrect for student quiz taking
                optDto.setIsCorrect(hideAnswers ? null : opt.isCorrect());
                return optDto;
            }).collect(Collectors.toList());
            dto.setOptions(optDtos);
        }

        return dto;
    }
}
