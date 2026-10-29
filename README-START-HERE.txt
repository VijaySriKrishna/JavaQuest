JavaQuest — corrected startup and quiz-selection build
======================================================

FIXES INCLUDED
- Category options are rendered by Thymeleaf on the server, so they are present even if browser JavaScript is slow to load.
- Opening /quiz now starts a default 10-minute quiz immediately. You can change category, difficulty, and time limit and click Apply settings to start a new attempt.
- Corrected the Thymeleaf JavaScript timer expression so the countdown is generated as valid JavaScript.
- Added a question-bank cache-buster to help browsers load the current 3,600-question bank.

REQUIREMENTS
- JDK 21 or newer (the project compiles with Java release 21).
- PostgreSQL running locally on port 5432.
- A PostgreSQL database named javaquest and a valid password for user postgres.
- Internet access the first time Maven Wrapper downloads Maven/dependencies.

DATABASE SETUP
1. Create a PostgreSQL database named javaquest if it does not already exist.
2. In the SAME PowerShell terminal used to start the app, set the password:

   $env:DB_PASSWORD = Read-Host "Enter your PostgreSQL password"

   Type the actual password when prompted. The characters may not be shown while typing.
3. Start the application from the folder containing pom.xml and mvnw.cmd:

   .\mvnw.cmd spring-boot:run

4. Open http://localhost:8080 in your browser and log in.

IMPORTANT
- Do not type the password into this README or commit it to source control.
- If you see `password authentication failed for user "postgres"`, the app's DB_PASSWORD value does not match the password for that PostgreSQL server/user. This is a database credential issue, not a Java compile error.
- The project requires a working PostgreSQL connection; it is not configured to silently bypass database authentication.
- The corrected source was inspected and the question bank's JavaScript syntax and record counts were checked. Maven build/tests could not be run in this environment because Maven Wrapper could not download Maven from Maven Central, so a clean runtime/build cannot honestly be guaranteed until you run it locally with network and PostgreSQL configured.
