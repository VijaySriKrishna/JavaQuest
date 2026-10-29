package com.javaquest.javaquest;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String username;
    private int score;
    private int total;
    @Column(nullable = false) private LocalDateTime attemptedAt;
    @Column(length = 80) private String category = "All Categories";
    @Column(length = 20) private String difficulty = "All Levels";
    private int durationSeconds;

    public QuizAttempt() {}
    public QuizAttempt(String username, int score, int total) {
        this(username, score, total, "All Categories", "All Levels", 0);
    }
    public QuizAttempt(String username, int score, int total, String category, String difficulty, int durationSeconds) {
        this.username = username; this.score = score; this.total = total;
        this.category = category; this.difficulty = difficulty; this.durationSeconds = durationSeconds;
        this.attemptedAt = LocalDateTime.now();
    }
    public Long getId(){return id;} public String getUsername(){return username;}
    public int getScore(){return score;} public int getTotal(){return total;}
    public LocalDateTime getAttemptedAt(){return attemptedAt;}
    public String getCategory(){return category;} public String getDifficulty(){return difficulty;}
    public int getDurationSeconds(){return durationSeconds;}
    public int getPercentage(){return total > 0 ? score * 100 / total : 0;}
}
