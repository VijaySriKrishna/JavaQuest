JAVAQUEST - UPDATED PROJECT NOTES

Added in this update:
- Category and difficulty selectors for quizzes.
- 5/10/15/20 minute quiz timer; the timer auto-submits when it expires.
- Server records the selected category, difficulty, elapsed duration and checks the recorded time limit before saving.
- Account-based leaderboard page with global/category/difficulty filters.
- Leaderboard navigation from the dashboard.
- Basic milestone achievements on the dashboard.
- Quiz attempts now persist category, difficulty and duration columns (Hibernate ddl-auto=update adds columns to an existing database).
- Database password is no longer hardcoded in application.properties.

IMPORTANT LIMITATIONS TO FINISH BEFORE PUBLIC DEPLOYMENT:
- The current quiz front end still calculates the score in JavaScript and passes it to /result. A determined user could alter the URL. For a fully secure leaderboard, move the question bank and answer checking into Java server-side code and submit answer IDs, never a client-computed score.
- Explanation text is not yet part of the question bank; add an explanation field for each question before showing per-answer explanations.
- Achievements currently represent milestones, not date-based daily streaks. A real streak requires storing attempt dates and calculating consecutive local calendar days.
- The current question bank has Easy and Medium labels in many sections; questions marked Hard need to be authored before the Hard filter has useful results.
- This project ZIP excludes the local database and build output. Existing data in your own database is not included here.

RUN SETUP (Windows PowerShell, from the inner javaquest folder):
1. Install PostgreSQL and ensure database 'javaquest' exists.
2. Set the database password in the current terminal before starting:
   $env:DB_PASSWORD = "YOUR_POSTGRES_PASSWORD"
3. Run:
   .\mvnw.cmd spring-boot:run
4. Open http://localhost:8080

Do not commit credentials to GitHub. For deployment, set DB_PASSWORD in the hosting provider's environment settings and use a hosted PostgreSQL database.
