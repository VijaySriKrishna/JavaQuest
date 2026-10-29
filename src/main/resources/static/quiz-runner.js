(() => {
  "use strict";
  const byId = id => document.getElementById(id);
  const panel = byId("quiz-panel");
  const questionText = byId("question-text");
  const optionsBox = byId("options");
  const countEl = byId("question-count");
  const answeredEl = byId("answered-count");
  const progress = byId("progress-fill");
  const prev = byId("previous-btn");
  const next = byId("next-btn");
  const message = byId("quiz-message");
  const timerEl = byId("timer");
  const categorySelect = byId("category-select");
  const difficultySelect = byId("difficulty-select");
  const minutesSelect = byId("minutes-select");
  const params = new URLSearchParams(location.search);
  const category = (params.get("category") || "All Categories").trim();
  const difficulty = (params.get("difficulty") || "All Levels").trim();
  const minutes = Math.max(1, Math.min(60, Number(params.get("minutes") || 10) || 10));
  panel.hidden = false;
  minutesSelect.value = String(minutes);
  difficultySelect.value = ["Easy", "Medium", "Hard", "All Levels"].includes(difficulty) ? difficulty : "All Levels";

  const bank = window.questions;
  if (!Array.isArray(bank) || bank.length === 0) {
    countEl.textContent = "Question bank failed to load";
    questionText.textContent = "JavaQuest couldn't load its question bank.";
    optionsBox.textContent = "Check that /questions.js loads successfully, then refresh this page.";
    prev.disabled = next.disabled = true;
    return;
  }

  const categories = [...new Set(bank.map(q => String(q.category || "").trim()).filter(Boolean))].sort();
  categorySelect.replaceChildren();
  const allCat = new Option("All Categories", "All Categories"); categorySelect.add(allCat);
  categories.forEach(c => categorySelect.add(new Option(c, c)));
  categorySelect.value = categories.includes(category) ? category : "All Categories";
  if (category !== "All Categories" && !categories.includes(category)) {
    questionText.textContent = `Category “${category}” wasn't found in the question bank.`;
    countEl.textContent = "Invalid category"; optionsBox.replaceChildren(); prev.disabled = next.disabled = true; return;
  }

  const norm = v => String(v ?? "").trim().toLowerCase();
  let filtered = bank.filter(q =>
    (category === "All Categories" || norm(q.category) === norm(category)) &&
    (difficulty === "All Levels" || norm(q.difficulty) === norm(difficulty)));
  if (!filtered.length) {
    countEl.textContent = "No matching questions";
    questionText.textContent = `No questions match ${category} / ${difficulty}. Choose another setting.`;
    optionsBox.replaceChildren(); prev.disabled = next.disabled = true; return;
  }
  // Fisher-Yates shuffle, then select at most ten distinct questions.
  filtered = filtered.slice();
  for (let i = filtered.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [filtered[i], filtered[j]] = [filtered[j], filtered[i]]; }
  const quiz = filtered.slice(0, Math.min(10, filtered.length));
  let index = 0; const answers = new Map(); let remaining = minutes * 60; let finished = false;
  const getOptions = q => Array.isArray(q.options) ? q.options : (Array.isArray(q.choices) ? q.choices : []);
  const optionText = o => typeof o === "object" && o !== null ? (o.value ?? o.text ?? o.label ?? "") : o;
  function render() {
    const q = quiz[index], opts = getOptions(q);
    countEl.textContent = `Question ${index + 1} of ${quiz.length}`;
    answeredEl.textContent = `${answers.size} answered`;
    progress.style.width = `${((index + 1) / quiz.length) * 100}%`;
    questionText.textContent = q.question ?? q.text ?? q.title ?? "Question text unavailable.";
    optionsBox.replaceChildren(); message.textContent = "";
    opts.forEach((opt, i) => {
      const label = document.createElement("label"); label.className = "option";
      const radio = document.createElement("input"); radio.type = "radio"; radio.name = "answer"; radio.checked = answers.get(index) === i;
      radio.addEventListener("change", () => { answers.set(index, i); answeredEl.textContent = `${answers.size} answered`; });
      const span = document.createElement("span"); span.textContent = String(optionText(opt)); label.append(radio, span); optionsBox.append(label);
    });
    prev.disabled = index === 0; next.disabled = opts.length === 0; next.textContent = index === quiz.length - 1 ? "Submit quiz →" : "Next →";
  }
  function finish(auto) {
    if (finished) return; finished = true; clearInterval(clock);
    let score = 0;
    quiz.forEach((q, i) => {
      const correct = q.answer ?? q.correctAnswer ?? q.correctOption ?? q.correct;
      const chosenIndex = answers.get(i); const opts = getOptions(q); const chosen = opts[chosenIndex];
      const chosenValue = chosen === undefined ? undefined : optionText(chosen);
      if (typeof correct === "number" && chosenIndex === correct) score++;
      else if (correct !== undefined && chosenValue !== undefined && String(chosenValue) === String(correct)) score++;
    });
    location.href = `/result?score=${score}&total=${quiz.length}&attempt=${Date.now()}&auto=${auto}`;
  }
  prev.addEventListener("click", () => { if (index > 0 && !finished) { index--; render(); } });
  next.addEventListener("click", () => {
    if (finished) return;
    if (index < quiz.length - 1) { index++; render(); return; }
    if (answers.size < quiz.length && !next.dataset.confirm) { message.textContent = "Unanswered questions count as incorrect. Click Submit quiz again to confirm."; next.textContent = "Confirm submission →"; next.dataset.confirm = "yes"; return; }
    finish(false);
  });
  function updateTimer() { timerEl.textContent = `${Math.floor(remaining / 60)}:${String(remaining % 60).padStart(2, "0")}`; if (remaining <= 60) timerEl.style.color = "#ef4444"; }
  updateTimer(); const clock = setInterval(() => { if (finished) return; remaining--; updateTimer(); if (remaining <= 0) finish(true); }, 1000);
  render();
})();
