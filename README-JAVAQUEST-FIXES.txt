JavaQuest - corrected project
==============================

This folder is the single Maven/Spring Boot project. Open this folder in VS Code:
JavaQuest/javaquest (the folder containing pom.xml and mvnw.cmd).

Question bank
-------------
The included src/main/resources/static/questions.js contains 3,600 records:
12 categories x 3 difficulties x 100 questions = 3,600.
The quiz filters by the selected category and difficulty, shuffles matching questions,
and takes up to 10 unique records for each attempt.

UI fixes included
-----------------
- The quiz question panel now respects the HTML hidden attribute despite its card CSS.
  Questions and the countdown remain hidden until Apply settings is submitted.
- The quiz page and leaderboard use the shared theme.js toggle and shared saved theme.
- Leaderboard category filter includes Exceptions.
- Quiz session timing is initialized only after settings are submitted, and invalid category
  or difficulty values are normalized.

Run on Windows
--------------
1. Install PostgreSQL and ensure the database named javaquest exists.
2. Open PowerShell in the folder containing pom.xml and mvnw.cmd.
3. Set the password for your local PostgreSQL `postgres` account for this terminal only:
      $env:DB_PASSWORD = Read-Host "Enter PostgreSQL password"
   Type the password when prompted. PowerShell does not show characters as you type.
4. Start the application:
      .\mvnw.cmd spring-boot:run
5. Open http://localhost:8080

If PostgreSQL says password authentication failed, the supplied password does not match
that PostgreSQL instance, or the instance is listening on a different port. This is separate
from the UI changes. Do not share your database password in screenshots or chat.

Build verification note
-----------------------
The source archive was inspected and the question bank counts were checked (3,600 entries,
36 category/difficulty groups, exactly 100 in each group, and 3,600 distinct question texts).
A Maven build could not be run in the preparation environment because the Maven wrapper
could not download Maven from Maven Central. Run the command above on your computer to verify.
