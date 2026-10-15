package com.example.quiz;

import com.example.quiz.entity.Option;
import com.example.quiz.entity.Question;
import com.example.quiz.entity.Quiz;
import com.example.quiz.entity.Result;
import com.example.quiz.entity.User;
import com.example.quiz.repository.QuizRepository;
import com.example.quiz.repository.ResultRepository;
import com.example.quiz.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class QuizApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedData(UserRepository userRepository,
                                     QuizRepository quizRepository,
                                     ResultRepository resultRepository,
                                     PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if users already seeded
            if (userRepository.count() > 0) {
                return;
            }

            System.out.println(">>> Seeding initial data for Online Quiz System...");

            // 1. Seed Users
            User admin = new User("admin", "admin@quiz.com", passwordEncoder.encode("admin123"), "System Administrator", "ROLE_ADMIN");
            User student = new User("student", "student@quiz.com", passwordEncoder.encode("student123"), "Alex Johnson", "ROLE_STUDENT");
            User student2 = new User("sarah", "sarah@quiz.com", passwordEncoder.encode("student123"), "Sarah Connor", "ROLE_STUDENT");

            userRepository.saveAll(List.of(admin, student, student2));

            // 2. Seed Quiz 1: Core Java & Spring Boot
            Quiz javaQuiz = new Quiz(
                    "Core Java & Spring Boot Essentials",
                    "Test your fundamental understanding of Java OOP principles, JVM mechanics, and Spring Boot architecture.",
                    "Java",
                    10,
                    60,
                    true
            );

            // Java Q1
            Question jq1 = new Question("Which Java feature enables an object to take many forms at runtime?", "Polymorphism allows methods to do different things based on the object that it is acting upon.");
            jq1.addOption(new Option("Encapsulation", false));
            jq1.addOption(new Option("Polymorphism", true));
            jq1.addOption(new Option("Inheritance", false));
            jq1.addOption(new Option("Abstraction", false));
            javaQuiz.addQuestion(jq1);

            // Java Q2
            Question jq2 = new Question("Which Spring Boot annotation marks a class as a RESTful web controller?", "@RestController is a convenience annotation that combines @Controller and @ResponseBody.");
            jq2.addOption(new Option("@Controller", false));
            jq2.addOption(new Option("@RestController", true));
            jq2.addOption(new Option("@Service", false));
            jq2.addOption(new Option("@Component", false));
            javaQuiz.addQuestion(jq2);

            // Java Q3
            Question jq3 = new Question("Which of the following is NOT a valid primitive data type in Java?", "String is a class and an Object in Java, not a primitive type like int, double, or boolean.");
            jq3.addOption(new Option("int", false));
            jq3.addOption(new Option("boolean", false));
            jq3.addOption(new Option("String", true));
            jq3.addOption(new Option("char", false));
            javaQuiz.addQuestion(jq3);

            // Java Q4
            Question jq4 = new Question("What does the 'IoC' concept in Spring stand for?", "Inversion of Control (IoC) is the pattern where program flow is controlled by an external container rather than direct user code.");
            jq4.addOption(new Option("Input of Control", false));
            jq4.addOption(new Option("Inversion of Control", true));
            jq4.addOption(new Option("Integration of Classes", false));
            jq4.addOption(new Option("Instance of Container", false));
            javaQuiz.addQuestion(jq4);

            // Java Q5
            Question jq5 = new Question("Which collection type does NOT allow duplicate elements?", "A Set in Java represents a mathematical set and guarantees uniqueness of elements.");
            jq5.addOption(new Option("ArrayList", false));
            jq5.addOption(new Option("LinkedList", false));
            jq5.addOption(new Option("HashSet", true));
            jq5.addOption(new Option("Vector", false));
            javaQuiz.addQuestion(jq5);

            quizRepository.save(javaQuiz);

            // 3. Seed Quiz 2: React JS & Modern Frontend
            Quiz reactQuiz = new Quiz(
                    "React JS & Modern Frontend Mastery",
                    "Assess your knowledge of React hooks, state management, component lifecycle, and virtual DOM concepts.",
                    "Frontend",
                    12,
                    70,
                    true
            );

            // React Q1
            Question rq1 = new Question("Which React Hook is used to perform side effects in functional components?", "useEffect is the hook designed for API calls, subscriptions, timers, and DOM mutations.");
            rq1.addOption(new Option("useState", false));
            rq1.addOption(new Option("useEffect", true));
            rq1.addOption(new Option("useMemo", false));
            rq1.addOption(new Option("useContext", false));
            reactQuiz.addQuestion(rq1);

            // React Q2
            Question rq2 = new Question("Why should a 'key' prop be used when rendering arrays of components in React?", "Keys help React identify which items have changed, been added, or been removed for optimal reconciliation.");
            rq2.addOption(new Option("To apply CSS styling", false));
            rq2.addOption(new Option("To help React uniquely identify items during reconciliation", true));
            rq2.addOption(new Option("To allow direct DOM access", false));
            rq2.addOption(new Option("To bind JavaScript events", false));
            reactQuiz.addQuestion(rq2);

            // React Q3
            Question rq3 = new Question("What is JSX in React?", "JSX is a syntax extension to JavaScript that lets you write HTML-like markup inside JS code.");
            rq3.addOption(new Option("A template engine like Thymeleaf", false));
            rq3.addOption(new Option("A syntax extension to JavaScript", true));
            rq3.addOption(new Option("A database query language", false));
            rq3.addOption(new Option("A CSS preprocessor", false));
            reactQuiz.addQuestion(rq3);

            // React Q4
            Question rq4 = new Question("How does data flow in a standard React component hierarchy?", "React enforces unidirectional data flow (top to bottom via props).");
            rq4.addOption(new Option("Bidirectional (two-way)", false));
            rq4.addOption(new Option("Unidirectional (parent to child)", true));
            rq4.addOption(new Option("Circular flow", false));
            rq4.addOption(new Option("Random broadcast", false));
            reactQuiz.addQuestion(rq4);

            quizRepository.save(reactQuiz);

            // 4. Seed Quiz 3: Database & SQL
            Quiz sqlQuiz = new Quiz(
                    "Database & SQL Fundamentals",
                    "Challenge your database fundamentals: relational models, indexing, transactions, and SQL queries.",
                    "Database",
                    10,
                    60,
                    true
            );

            // SQL Q1
            Question sq1 = new Question("Which SQL clause is used to filter grouped records created by GROUP BY?", "HAVING filters groups created by GROUP BY, whereas WHERE filters individual rows before grouping.");
            sq1.addOption(new Option("WHERE", false));
            sq1.addOption(new Option("HAVING", true));
            sq1.addOption(new Option("FILTER", false));
            sq1.addOption(new Option("ORDER BY", false));
            sqlQuiz.addQuestion(sq1);

            // SQL Q2
            Question sq2 = new Question("What does the 'A' in ACID properties of database transactions stand for?", "Atomicity ensures that all statements in a transaction either complete successfully or roll back completely.");
            sq2.addOption(new Option("Availability", false));
            sq2.addOption(new Option("Atomicity", true));
            sq2.addOption(new Option("Authentication", false));
            sq2.addOption(new Option("Accuracy", false));
            sqlQuiz.addQuestion(sq2);

            quizRepository.save(sqlQuiz);

            // 5. Seed sample past result for student
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> mockDetails = List.of(
                    Map.of(
                            "questionId", jq1.getId(),
                            "questionText", jq1.getQuestionText(),
                            "selectedOptionText", "Polymorphism",
                            "correctOptionText", "Polymorphism",
                            "isCorrect", true,
                            "explanation", jq1.getExplanation()
                    ),
                    Map.of(
                            "questionId", jq2.getId(),
                            "questionText", jq2.getQuestionText(),
                            "selectedOptionText", "@RestController",
                            "correctOptionText", "@RestController",
                            "isCorrect", true,
                            "explanation", jq2.getExplanation()
                    ),
                    Map.of(
                            "questionId", jq3.getId(),
                            "questionText", jq3.getQuestionText(),
                            "selectedOptionText", "String",
                            "correctOptionText", "String",
                            "isCorrect", true,
                            "explanation", jq3.getExplanation()
                    ),
                    Map.of(
                            "questionId", jq4.getId(),
                            "questionText", jq4.getQuestionText(),
                            "selectedOptionText", "Integration of Classes",
                            "correctOptionText", "Inversion of Control",
                            "isCorrect", false,
                            "explanation", jq4.getExplanation()
                    ),
                    Map.of(
                            "questionId", jq5.getId(),
                            "questionText", jq5.getQuestionText(),
                            "selectedOptionText", "HashSet",
                            "correctOptionText", "HashSet",
                            "isCorrect", true,
                            "explanation", jq5.getExplanation()
                    )
            );

            Result sampleResult = new Result(
                    student,
                    javaQuiz,
                    80,
                    5,
                    4,
                    true,
                    mapper.writeValueAsString(mockDetails)
            );
            resultRepository.save(sampleResult);

            System.out.println(">>> Online Quiz System sample data successfully seeded!");
            System.out.println(">>> Default Admin: admin / admin123");
            System.out.println(">>> Default Student: student / student123");
        };
    }
}
