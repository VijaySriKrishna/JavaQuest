package com.javaquest.javaquest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUsernameOrderByAttemptedAtDesc(String username);
    List<QuizAttempt> findTop100ByOrderByScoreDescAttemptedAtAsc();
    @Query("select a from QuizAttempt a where a.category = :category order by a.score desc, a.attemptedAt asc")
    List<QuizAttempt> leaderboardByCategory(@Param("category") String category);
    @Query("select a from QuizAttempt a where a.difficulty = :difficulty order by a.score desc, a.attemptedAt asc")
    List<QuizAttempt> leaderboardByDifficulty(@Param("difficulty") String difficulty);
}
