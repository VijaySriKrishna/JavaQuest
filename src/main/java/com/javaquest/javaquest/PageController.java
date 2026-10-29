
package com.javaquest.javaquest;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
public class PageController {

    private final UserAccountRepository accountRepository;
    private final QuizAttemptRepository attemptRepository;
    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public PageController(
            UserAccountRepository accountRepository,
            QuizAttemptRepository attemptRepository) {
        this.accountRepository = accountRepository;
        this.attemptRepository = attemptRepository;
    }

    @GetMapping("/")
    public String home() {
        return "login";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        username = username.trim();
        email = email.trim().toLowerCase(java.util.Locale.ROOT);

        if (username.length() < 3 || password.length() < 6) {
            model.addAttribute("error",
                    "Username needs 3 characters and password needs 6!");
            return "register";
        }

        if (!email.matches("^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+$")) {
            model.addAttribute("error", "Enter a valid email address.");
            return "register";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "register";
        }

        if (accountRepository.existsByUsername(username)) {
            model.addAttribute("error", "Username already exists!");
            return "register";
        }

        if (accountRepository.existsByEmailIgnoreCase(email)) {
            model.addAttribute("error", "An account with that email already exists!");
            return "register";
        }

        UserAccount account = new UserAccount(
                username, email, passwordEncoder.encode(password));

        accountRepository.save(account);

        return "redirect:/login?registered";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String identifier,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        identifier = identifier.trim();

        UserAccount account = identifier.contains("@")
                ? accountRepository.findByEmailIgnoreCase(identifier).orElse(null)
                : accountRepository.findByUsername(identifier).orElse(null);

        if (account != null &&
                passwordEncoder.matches(password, account.getPasswordHash())) {
            session.setAttribute("username", account.getUsername());
            session.removeAttribute("lastAttempt");
            return "redirect:/dashboard";
        }

        model.addAttribute("error",
                "Invalid email/username or password.");
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String username = getUsername(session);
        if (username == null) return "redirect:/login";

        List<QuizAttempt> attempts =
                attemptRepository.findByUsernameOrderByAttemptedAtDesc(username);

        int totalAttempts = attempts.size();
        int bestPercentage = 0;
        int percentageSum = 0;

        for (QuizAttempt a : attempts) {
            int percentage = a.getPercentage();
            bestPercentage = Math.max(bestPercentage, percentage);
            percentageSum += percentage;
        }

        int averagePercentage = totalAttempts == 0
                ? 0 : percentageSum / totalAttempts;

        model.addAttribute("username", username);
        model.addAttribute("totalAttempts", totalAttempts);
        model.addAttribute("bestPercentage", bestPercentage);
        model.addAttribute("averagePercentage", averagePercentage);
        model.addAttribute("latestAttempt",
                attempts.isEmpty() ? null : attempts.get(0));
        List<String> achievements = new ArrayList<>();
        if (totalAttempts >= 1) achievements.add("First Steps · completed your first quiz");
        if (totalAttempts >= 10) achievements.add("Quiz Explorer · completed 10 quizzes");
        if (bestPercentage == 100) achievements.add("Perfect Score · earned 100% on an attempt");
        if (totalAttempts >= 5) achievements.add("Consistent Learner · completed 5 quizzes");
        model.addAttribute("achievements", achievements);
        model.addAttribute("achievementCount", achievements.size());

        return "dashboard";
    }

    @GetMapping("/quiz")
    public String showQuiz(@RequestParam(required = false) String category,
                           @RequestParam(required = false) String difficulty,
                           @RequestParam(required = false) Integer minutes,
                           HttpSession session, Model model) {
        String username = getUsername(session);
        if (username == null) return "redirect:/login";

        model.addAttribute("username", username);
        // Start a default quiz immediately on /quiz; settings can still be changed with the form.
        boolean settingsApplied = true;
        String selectedCategory = (category == null || category.isBlank()) ? "All Categories" : category.trim();
        String selectedDifficulty = (difficulty == null || difficulty.isBlank()) ? "All Levels" : difficulty.trim();
        int selectedMinutes = (minutes == null || minutes < 1 || minutes > 60) ? 10 : minutes;
        List<String> validCategories = List.of("Java Basics", "Data Types", "Operators", "Control Flow",
                "Arrays and Strings", "OOP", "Inheritance", "Exceptions", "Collections",
                "File Handling", "Threads", "Java Features");
        List<String> categoryOptions = new ArrayList<>(validCategories);
        categoryOptions.add("All Categories");
        List<String> validDifficulties = List.of("Easy", "Medium", "Hard", "All Levels");
        if (!validCategories.contains(selectedCategory)) selectedCategory = "All Categories";
        if (!validDifficulties.contains(selectedDifficulty)) selectedDifficulty = "All Levels";
        model.addAttribute("settingsApplied", settingsApplied);
        model.addAttribute("categories", categoryOptions);
        if (settingsApplied) {
            session.setAttribute("quizStartedAt", System.currentTimeMillis());
            session.setAttribute("quizCategory", selectedCategory);
            session.setAttribute("quizDifficulty", selectedDifficulty);
            session.setAttribute("quizLimitSeconds", selectedMinutes * 60);
            session.removeAttribute("lastAttempt");
        }
        model.addAttribute("category", selectedCategory);
        model.addAttribute("difficulty", selectedDifficulty);
        model.addAttribute("minutes", selectedMinutes);
        return "quiz";
    }

    @GetMapping("/leaderboard")
    public String leaderboard(@RequestParam(required = false) String category,
                              @RequestParam(required = false) String difficulty,
                              HttpSession session, Model model) {
        String username = getUsername(session);
        if (username == null) return "redirect:/login";
        List<QuizAttempt> rows;
        if (category != null && !category.isBlank()) rows = attemptRepository.leaderboardByCategory(category);
        else if (difficulty != null && !difficulty.isBlank()) rows = attemptRepository.leaderboardByDifficulty(difficulty);
        else rows = attemptRepository.findTop100ByOrderByScoreDescAttemptedAtAsc();
        Map<String, QuizAttempt> bestByUser = new LinkedHashMap<>();
        for (QuizAttempt row : rows) bestByUser.putIfAbsent(row.getUsername(), row);
        model.addAttribute("username", username);
        model.addAttribute("leaderboard", new ArrayList<>(bestByUser.values()));
        model.addAttribute("selectedCategory", category == null ? "" : category);
        model.addAttribute("selectedDifficulty", difficulty == null ? "" : difficulty);
        return "leaderboard";
    }

    @GetMapping("/result")
    public String result(
            @RequestParam int score,
            @RequestParam int total,
            @RequestParam long attempt,
            HttpSession session,
            Model model) {

        String username = getUsername(session);
        if (username == null) return "redirect:/login";

        if (total <= 0 || score < 0 || score > total) {
            return "redirect:/quiz";
        }

        String attemptKey = username + ":" + attempt;
        String lastAttempt = (String) session.getAttribute("lastAttempt");

        if (!attemptKey.equals(lastAttempt)) {
            String category = (String) session.getAttribute("quizCategory");
            String difficulty = (String) session.getAttribute("quizDifficulty");
            Long started = (Long) session.getAttribute("quizStartedAt");
            Integer limit = (Integer) session.getAttribute("quizLimitSeconds");
            int duration = started == null ? 0 : (int) Math.max(0, (System.currentTimeMillis() - started) / 1000);
            if (limit != null && duration > limit + 5) { score = 0; }
            attemptRepository.save(new QuizAttempt(username, score, total,
                    category == null ? "All Categories" : category,
                    difficulty == null ? "All Levels" : difficulty, duration));
            session.setAttribute("lastAttempt", attemptKey);
        }

        int percentage = score * 100 / total;

        model.addAttribute("username", username);
        model.addAttribute("score", score);
        model.addAttribute("total", total);
        model.addAttribute("percentage", percentage);

        if (percentage >= 80) {
            model.addAttribute("message",
                    "Excellent work! Java master in the making.");
        } else if (percentage >= 50) {
            model.addAttribute("message",
                    "Good effort! Keep practising.");
        } else {
            model.addAttribute("message",
                    "Keep going! Every attempt makes you better.");
        }

        return "result";
    }

    @GetMapping("/history")
    public String showHistory(HttpSession session, Model model) {
        String username = getUsername(session);
        if (username == null) return "redirect:/login";

        model.addAttribute("username", username);
        model.addAttribute("attempts",
                attemptRepository.findByUsernameOrderByAttemptedAtDesc(username));

        return "history";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    private String getUsername(HttpSession session) {
        return (String) session.getAttribute("username");
    }
}
